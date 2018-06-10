/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lutron.internal.mapping;

import java.util.Optional;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.type.ChannelKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapping between lutron button and openhab channel.
 *
 * @author Łukasz Dywicki - initial contribution
 */
public class ButtonMapping extends ChannelMapping {

    private final Logger logger = LoggerFactory.getLogger(ButtonMapping.class);

    private static final Integer ACTION_PRESS = 3;
    private static final Integer ACTION_RELEASE = 4;

    private final static Switch SWITCH_ON = new SimpleSwitch(ACTION_PRESS);
    private final static Switch SWITCH_OFF = new SimpleSwitch(ACTION_RELEASE);

    public ButtonMapping(int component, String channel) {
        super(component, channel);
    }

    @Override
    public Optional<ChannelKind> getKind() {
        return Optional.of(ChannelKind.TRIGGER);
    }

    @Override
    public Switch getArguments(OnOffType state) {
        if (state == OnOffType.ON) {
            return SWITCH_ON;
        }
        return SWITCH_OFF;
    }

    @Override
    public Optional<Integer> getStateCommand() {
        return Optional.empty();
    }

    @Override
    public Optional<OnOffType> parse(String[] parameters) {
        if (parameters.length == 2) {
            try {
                int action = Integer.parseInt(parameters[1]);
                if (ACTION_PRESS.equals(action)) {
                    return Optional.ofNullable(OnOffType.ON);
                }
                return Optional.ofNullable(OnOffType.OFF);
            } catch (NumberFormatException e) {
                logger.warn("Couldn't parse state update from lutron: {}", new Object[] { parameters });
            }
        }
        return Optional.empty();
    }

}
