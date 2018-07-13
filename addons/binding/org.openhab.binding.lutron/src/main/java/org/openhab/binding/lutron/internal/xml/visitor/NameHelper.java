package org.openhab.binding.lutron.internal.xml.visitor;

import org.openhab.binding.lutron.internal.discovery.project.Button;
import org.openhab.binding.lutron.internal.discovery.project.Component;
import org.openhab.binding.lutron.internal.discovery.project.Device;

public class NameHelper {

    public static String getItemName(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\-]", "_");
    }

    public static String getItemName(Device device, Button button) {
        return getItemName("keypad_" + device.getIntegrationId() + "_" + button.getName());
    }

    public static Object getItemName(Device device, Component component) {
        return getItemName("keypad_" + device.getIntegrationId() + "_"
                + component.getComponentType().name().toLowerCase() + "_" + component.getComponentNumber());
    }

}
