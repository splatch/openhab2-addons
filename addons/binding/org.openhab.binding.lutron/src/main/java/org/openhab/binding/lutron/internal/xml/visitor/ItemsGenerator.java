package org.openhab.binding.lutron.internal.xml.visitor;

import java.util.function.Predicate;

import org.openhab.binding.lutron.internal.discovery.project.Area;
import org.openhab.binding.lutron.internal.discovery.project.Button;
import org.openhab.binding.lutron.internal.discovery.project.Component;
import org.openhab.binding.lutron.internal.discovery.project.Device;
import org.openhab.binding.lutron.internal.discovery.project.DeviceGroup;
import org.openhab.binding.lutron.internal.discovery.project.DeviceType;
import org.openhab.binding.lutron.internal.discovery.project.LED;
import org.openhab.binding.lutron.internal.discovery.project.Project;

public class ItemsGenerator implements Visitor {

    private static Predicate<Device> deviceType(DeviceType type) {
        return device -> {
            DeviceType deviceType = device.getDeviceType();
            return deviceType != null && deviceType.equals(type);
        };
    }

    private Device device;
    private Component component;

    @Override
    public void visit(Project project) {
    }

    @Override
    public void leave(Project project) {
    }

    @Override
    public void visit(Area area) {
    }

    @Override
    public void visit(DeviceGroup deviceGroup) {
    }

    @Override
    public void visit(Device device) {
        this.device = device;
    }

    @Override
    public void leave(Device device) {
        this.device = null;
    }

    @Override
    public void visit(Component component) {
        this.component = component;
    }

    @Override
    public void visit(Button button) {
        System.out.println(String.format("Switch %s \"%s\" { channel=\"lutron:keypad:k_%d:button%s\"}",
                NameHelper.getItemName(device, component), button.getLabel(), device.getIntegrationId(),
                component.getComponentNumber()));
    }

    @Override
    public void visit(LED led) {
        int ledId = component.getComponentNumber() - 80;
        System.out.println(String.format("Switch %s \"%s\" { channel=\"lutron:keypad:k_%d:led%s\"}",
                NameHelper.getItemName(device, component), "LED " + ledId, device.getIntegrationId(), ledId));
    }

    @Override
    public boolean shouldEnter(Project project) {
        return true;
    }

    @Override
    public boolean shouldEnter(Area area) {
        return true;
    }

    @Override
    public boolean shouldEnter(DeviceGroup deviceGroup) {
        return true;
    }

    @Override
    public boolean shouldEnter(Device device) {
        return true;
    }

    @Override
    public boolean shouldEnter(Component component) {
        return true;
    }

    @Override
    public boolean shouldEnter(Button button) {
        int componentId = component.getComponentNumber();
        return componentId < 8;
    }

    @Override
    public boolean shouldEnter(LED led) {
        return true;
    }

}
