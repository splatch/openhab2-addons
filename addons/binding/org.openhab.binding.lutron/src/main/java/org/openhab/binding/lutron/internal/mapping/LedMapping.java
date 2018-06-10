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
 * Mapping between lutron led and openhab channel.
 *
 * @author Łukasz Dywicki - initial contribution
 */
public class LedMapping extends ChannelMapping {

    private final Logger logger = LoggerFactory.getLogger(LedMapping.class);

    private static final Integer LED_STATE = 9;
    private static final Integer LED_OFF = 0;
    private static final Integer LED_ON = 1;
    // currently unsupported
    private static final Integer LED_NORMAL_FLASH = 2;
    private static final Integer LED_RAPID_FLASH = 3;

    private final static Switch SWITCH_ON = new SimpleSwitch(LED_STATE, LED_ON);
    private final static Switch SWITCH_OFF = new SimpleSwitch(LED_STATE, LED_OFF);

    public LedMapping(int component, String channel) {
        super(component, channel);
    }

    @Override
    public Optional<ChannelKind> getKind() {
        return Optional.of(ChannelKind.STATE);
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
        return Optional.ofNullable(LED_STATE);
    }

    @Override
    public Optional<OnOffType> parse(String[] parameters) {
        if (parameters.length >= 3) {
            try {
                if (LED_STATE.equals(Integer.parseInt(parameters[1]))) {
                    int state = Integer.parseInt(parameters[2]);
                    if (LED_OFF.equals(state)) {
                        return Optional.ofNullable(OnOffType.OFF);
                    }
                    // there is also blink and blink fast
                    return Optional.ofNullable(OnOffType.ON);
                }
            } catch (NumberFormatException e) {
                logger.warn("Couldn't parse lutron action value. Integer number expected but not found: {}.",
                        new Object[] { parameters });
            }
        }
        return Optional.empty();
    }

}
