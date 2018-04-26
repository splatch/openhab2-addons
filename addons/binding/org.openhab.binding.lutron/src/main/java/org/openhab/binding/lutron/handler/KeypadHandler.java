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

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.lutron.internal.mapping.ChannelMapping;
import org.openhab.binding.lutron.internal.mapping.ListMappingSet;
import org.openhab.binding.lutron.internal.mapping.MappingSet;
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

    private static final MappingSet MAPPING = new ListMappingSet(new ChannelMapping(COMPONENT_BUTTON1, CHANNEL_BUTTON1),
            new ChannelMapping(COMPONENT_BUTTON2, CHANNEL_BUTTON2),
            new ChannelMapping(COMPONENT_BUTTON3, CHANNEL_BUTTON3),
            new ChannelMapping(COMPONENT_BUTTON4, CHANNEL_BUTTON4),
            new ChannelMapping(COMPONENT_BUTTON5, CHANNEL_BUTTON5),
            new ChannelMapping(COMPONENT_BUTTON6, CHANNEL_BUTTON6),
            new ChannelMapping(COMPONENT_BUTTON7, CHANNEL_BUTTON7),
            new ChannelMapping(COMPONENT_BUTTONTOPRAISE, CHANNEL_BUTTONTOPRAISE),
            new ChannelMapping(COMPONENT_BUTTONTOPLOWER, CHANNEL_BUTTONTOPLOWER),
            new ChannelMapping(COMPONENT_BUTTONBOTTOMRAISE, CHANNEL_BUTTONBOTTOMRAISE),
            new ChannelMapping(COMPONENT_BUTTONBOTTOMLOWER, CHANNEL_BUTTONBOTTOMLOWER),
            new ChannelMapping(COMPONENT_LED1, CHANNEL_LED1), new ChannelMapping(COMPONENT_LED2, CHANNEL_LED2),
            new ChannelMapping(COMPONENT_LED3, CHANNEL_LED3), new ChannelMapping(COMPONENT_LED4, CHANNEL_LED4),
            new ChannelMapping(COMPONENT_LED5, CHANNEL_LED5), new ChannelMapping(COMPONENT_LED6, CHANNEL_LED6),
            new ChannelMapping(COMPONENT_LED7, CHANNEL_LED7));

    private static final Integer ACTION_PRESS = 3;
    private static final Integer ACTION_RELEASE = 4;
    private static final Integer LED_STATE = 9;

    private static final Integer LED_OFF = 0;
    private static final Integer LED_ON = 1;

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

        updateStatus(ThingStatus.ONLINE);

        MAPPING.all().map(ChannelMapping::getComponent).forEach(component -> queryDevice(component, LED_STATE));
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
        Optional<Integer> component = MAPPING.fromChannel(channelUID).map(ChannelMapping::getComponent);

        if (component.isPresent()) {
            if (command instanceof OnOffType) {
                OnOffType state = (OnOffType) command;
                Integer componentId = component.get();

                device(componentId, LED_STATE, state == OnOffType.ON ? 1 : 0);
            }
        }
    }

    @Override
    public int getIntegrationId() {
        return this.integrationId;
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        MAPPING.fromChannel(channelUID).map(ChannelMapping::getComponent)
                .ifPresent(component -> queryDevice(component, LED_STATE));
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

            ChannelUID channelUID = channelFromComponent(component);

            if (channelUID != null) {
                if (LED_STATE.toString().equals(parameters[1]) && parameters.length >= 3) {
                    if (LED_ON.toString().equals(parameters[2])) {
                        updateState(channelUID, OnOffType.ON);
                    } else if (LED_OFF.toString().equals(parameters[2])) {
                        updateState(channelUID, OnOffType.OFF);
                    }
                } else if (ACTION_PRESS.toString().equals(parameters[1])) {
                    postCommand(channelUID, OnOffType.ON);
                } else if (ACTION_RELEASE.toString().equals(parameters[1])) {
                    postCommand(channelUID, OnOffType.OFF);
                }
            }
        }
    }

}
