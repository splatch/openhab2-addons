/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lutron.internal.mapping;

import org.eclipse.smarthome.core.thing.ChannelUID;

/**
 * Mapping between lutron component and openhab channel.
 *
 * @author Łukasz Dywicki - initial contribution
 */
public class ChannelMapping {

    private final int component;
    private final String channel;

    public ChannelMapping(int component, String channel) {
        this.component = component;
        this.channel = channel;

    }

    public int getComponent() {
        return component;
    }

    public String getChannel() {
        return channel;
    }

    public boolean matches(ChannelUID channel) {
        return this.channel.equals(channel.getId());
    }

    public boolean matches(int component) {
        return this.component == component;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        result = prime * result + component;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChannelMapping other = (ChannelMapping) obj;
        if (channel == null) {
            if (other.channel != null) {
                return false;
            }
        } else if (!channel.equals(other.channel)) {
            return false;
        }
        if (component != other.component) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ChannelMapping [component=" + component + ", channel=" + channel + "]";
    }

}
