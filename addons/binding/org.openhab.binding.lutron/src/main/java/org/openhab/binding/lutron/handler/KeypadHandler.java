/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lutron.handler;

import static org.openhab.binding.lutron.LutronBindingConstants.*;

import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelKind;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.lutron.internal.mapping.ButtonMapping;
import org.openhab.binding.lutron.internal.mapping.ChannelMapping;
import org.openhab.binding.lutron.internal.mapping.LedMapping;
import org.openhab.binding.lutron.internal.mapping.ListMappingSet;
import org.openhab.binding.lutron.internal.mapping.MappingSet;
import org.openhab.binding.lutron.internal.mapping.Switch;
import org.openhab.binding.lutron.internal.protocol.LutronCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler responsible for communicating with a keypad.
 *
 * @author Allan Tong - Initial contribution
 */
public class KeypadHandler extends LutronHandler {

    private static final int COMPONENT_BUTTON1 = 1;
    private static final int COMPONENT_BUTTON2 = 2;
    private static final int COMPONENT_BUTTON3 = 3;
    private static final int COMPONENT_BUTTON4 = 4;
    private static final int COMPONENT_BUTTON5 = 5;
    private static final int COMPONENT_BUTTON6 = 6;
    private static final int COMPONENT_BUTTON7 = 7;
    private static final int COMPONENT_BUTTONTOPLOWER = 16;
    private static final int COMPONENT_BUTTONTOPRAISE = 17;
    private static final int COMPONENT_BUTTONBOTTOMLOWER = 18;
    private static final int COMPONENT_BUTTONBOTTOMRAISE = 19;
    private static final int COMPONENT_LED1 = 81;
    private static final int COMPONENT_LED2 = 82;
    private static final int COMPONENT_LED3 = 83;
    private static final int COMPONENT_LED4 = 84;
    private static final int COMPONENT_LED5 = 85;
    private static final int COMPONENT_LED6 = 86;
    private static final int COMPONENT_LED7 = 87;

    private static final MappingSet MAPPING = new ListMappingSet(new ButtonMapping(COMPONENT_BUTTON1, CHANNEL_BUTTON1),
            new ButtonMapping(COMPONENT_BUTTON2, CHANNEL_BUTTON2),
            new ButtonMapping(COMPONENT_BUTTON3, CHANNEL_BUTTON3),
            new ButtonMapping(COMPONENT_BUTTON4, CHANNEL_BUTTON4),
            new ButtonMapping(COMPONENT_BUTTON5, CHANNEL_BUTTON5),
            new ButtonMapping(COMPONENT_BUTTON6, CHANNEL_BUTTON6),
            new ButtonMapping(COMPONENT_BUTTON7, CHANNEL_BUTTON7),
            new ButtonMapping(COMPONENT_BUTTONTOPRAISE, CHANNEL_BUTTONTOPRAISE),
            new ButtonMapping(COMPONENT_BUTTONTOPLOWER, CHANNEL_BUTTONTOPLOWER),
            new ButtonMapping(COMPONENT_BUTTONBOTTOMRAISE, CHANNEL_BUTTONBOTTOMRAISE),
            new ButtonMapping(COMPONENT_BUTTONBOTTOMLOWER, CHANNEL_BUTTONBOTTOMLOWER),
            new LedMapping(COMPONENT_LED1, CHANNEL_LED1), new LedMapping(COMPONENT_LED2, CHANNEL_LED2),
            new LedMapping(COMPONENT_LED3, CHANNEL_LED3), new LedMapping(COMPONENT_LED4, CHANNEL_LED4),
            new LedMapping(COMPONENT_LED5, CHANNEL_LED5), new LedMapping(COMPONENT_LED6, CHANNEL_LED6),
            new LedMapping(COMPONENT_LED7, CHANNEL_LED7));

    private final Logger logger = LoggerFactory.getLogger(KeypadHandler.class);

    private int integrationId;

    public KeypadHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        Number id = (Number) getThing().getConfiguration().get("integrationId");

        if (id == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "No integrationId");

            return;
        }

        this.integrationId = id.intValue();

        ThingBuilder thing = editThing();

        // Channel channel = ChannelBuilder.create(channelUID, acceptedItem).withConfiguration(configuration)
        // .withDefaultTags(defaultTags).withDescription(description != null ? description : "").withKind(kind)
        // .withLabel(channelName).withProperties(properties).withType(type).build();
        // thingBuilder.withoutChannel(channelUID).withChannel(channel);
        // updateThing(thingBuilder.build());

        for (ChannelMapping mapping : MAPPING.all().collect(Collectors.toList())) {
            ChannelBuilder channelBuilder = ChannelBuilder.create(new ChannelUID(mapping.getChannel()), "switch");
            Optional<ChannelKind> kind = mapping.getKind();
            if (kind.isPresent()) {
                channelBuilder.withKind(kind.get());
            }
            thing.withChannel(channelBuilder.build());
        }

        updateThing(thing.build());

        MAPPING.all().filter(mapping -> mapping.getStateCommand().isPresent())
                .forEach(mapping -> queryDevice(mapping.getComponent(), mapping.getStateCommand().get()));

        updateStatus(ThingStatus.ONLINE);
    }

    private ChannelUID channelFromComponent(int component) {
        Optional<ChannelUID> channelUID = MAPPING.fromComponent(component).map(ChannelMapping::getChannel)
                .map(channel -> new ChannelUID(getThing().getUID(), channel));

        if (channelUID.isPresent()) {
            return channelUID.get();
        }

        this.logger.error("Unknown component {}", component);
        return null;
    }

    @Override
    public void handleCommand(final ChannelUID channelUID, Command command) {
        Optional<ChannelMapping> component = MAPPING.fromChannel(channelUID);

        if (component.isPresent()) {
            if (command instanceof OnOffType) {
                OnOffType state = (OnOffType) command;
                ChannelMapping mapping = component.get();

                Switch lutronState = mapping.getArguments(state);
                Optional<Integer> argument = lutronState.getParameter();
                if (argument.isPresent()) {
                    device(mapping.getComponent(), lutronState.getAction(), argument.get());
                } else {
                    device(mapping.getComponent(), lutronState.getAction());
                }
            }
        }
    }

    @Override
    public int getIntegrationId() {
        return this.integrationId;
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        MAPPING.fromChannel(channelUID).filter(mapping -> mapping.getStateCommand().isPresent())
                .ifPresent(mapping -> queryDevice(mapping.getComponent(), mapping.getStateCommand().get()));
    }

    @Override
    public void handleUpdate(LutronCommandType type, String... parameters) {
        if (type == LutronCommandType.DEVICE && parameters.length >= 2) {
            int component;

            try {
                component = Integer.parseInt(parameters[0]);
            } catch (NumberFormatException e) {
                this.logger.error("Invalid component {} in keypad update event message", parameters[0]);

                return;
            }

            Optional<ChannelMapping> channelMapping = MAPPING.fromComponent(component);
            Optional<ChannelUID> channel = channelMapping.map(ChannelMapping::getChannel)
                    .map(channelName -> new ChannelUID(getThing().getUID(), channelName));

            channelMapping.flatMap(mapping -> mapping.parse(parameters))
                    .ifPresent(state -> updateState(channel.get(), state));

            /*
             * if (channelUID != null) {
             * if (LED_STATE.toString().equals(parameters[1]) && parameters.length >= 3) {
             * if (LED_ON.toString().equals(parameters[2])) {
             * updateState(channelUID, OnOffType.ON);
             * } else if (LED_OFF.toString().equals(parameters[2])) {
             * updateState(channelUID, OnOffType.OFF);
             * }
             * } else if (ACTION_PRESS.toString().equals(parameters[1])) {
             * postCommand(channelUID, OnOffType.ON);
             * } else if (ACTION_RELEASE.toString().equals(parameters[1])) {
             * postCommand(channelUID, OnOffType.OFF);
             * }
             * }
             */

        }
    }

}
