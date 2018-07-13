/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lutron.internal.discovery.project;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.openhab.binding.lutron.internal.xml.visitor.Visitor;

/**
 * An input device in a Lutron system such as a keypad or occupancy sensor.
 *
 * @author Allan Tong - Initial contribution
 */
public class Device implements DeviceNode {
    private String name;
    private Integer integrationId;
    private String type;
    private List<Component> components;

    public String getName() {
        return name;
    }

    public Integer getIntegrationId() {
        return integrationId;
    }

    public String getType() {
        return type;
    }

    public DeviceType getDeviceType() {
        try {
            return DeviceType.valueOf(this.type);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Component> getComponents() {
        return components != null ? components : Collections.<Component> emptyList();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components, integrationId, name, type);
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
        Device other = (Device) obj;
        if (components == null) {
            if (other.components != null) {
                return false;
            }
        } else if (!components.equals(other.components)) {
            return false;
        }
        if (integrationId == null) {
            if (other.integrationId != null) {
                return false;
            }
        } else if (!integrationId.equals(other.integrationId)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
