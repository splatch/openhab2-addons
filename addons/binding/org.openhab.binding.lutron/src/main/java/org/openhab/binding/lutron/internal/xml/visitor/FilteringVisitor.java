package org.openhab.binding.lutron.internal.xml.visitor;

import java.util.function.Predicate;

import org.openhab.binding.lutron.internal.discovery.project.Area;
import org.openhab.binding.lutron.internal.discovery.project.Button;
import org.openhab.binding.lutron.internal.discovery.project.Component;
import org.openhab.binding.lutron.internal.discovery.project.Device;
import org.openhab.binding.lutron.internal.discovery.project.DeviceGroup;
import org.openhab.binding.lutron.internal.discovery.project.LED;
import org.openhab.binding.lutron.internal.discovery.project.Project;

public class FilteringVisitor implements Visitor {

    private final Visitor delegate;

    private Predicate<Project> projectFilter;
    private Predicate<Area> areaFilter;
    private Predicate<DeviceGroup> deviceGroupFilter;
    private Predicate<Device> deviceFilter;
    private Predicate<Component> componentFilter;
    private Predicate<Button> buttonFilter;
    private Predicate<LED> ledFilter;

    public FilteringVisitor(Visitor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void visit(Project project) {
        delegate.visit(project);
    }

    @Override
    public void leave(Project project) {
        delegate.leave(project);
    }

    @Override
    public void visit(Area area) {
        delegate.visit(area);
    }

    @Override
    public void visit(DeviceGroup deviceGroup) {
        delegate.visit(deviceGroup);
    }

    @Override
    public void visit(Device device) {
        delegate.visit(device);
    }

    @Override
    public void leave(Device device) {
        delegate.leave(device);
    }

    @Override
    public void visit(Component component) {
        delegate.visit(component);
    }

    @Override
    public void visit(Button button) {
        delegate.visit(button);
    }

    @Override
    public void visit(LED led) {
        delegate.visit(led);
    }

    public void setProjectFilter(Predicate<Project> projectFilter) {
        this.projectFilter = projectFilter;
    }

    public void setAreaFilter(Predicate<Area> areaFilter) {
        this.areaFilter = areaFilter;
    }

    public void setDeviceGroupFilter(Predicate<DeviceGroup> deviceGroupFilter) {
        this.deviceGroupFilter = deviceGroupFilter;
    }

    public void setDeviceFilter(Predicate<Device> deviceFilter) {
        this.deviceFilter = deviceFilter;
    }

    public void setComponentFilter(Predicate<Component> componentFilter) {
        this.componentFilter = componentFilter;
    }

    public void setButtonFilter(Predicate<Button> buttonFilter) {
        this.buttonFilter = buttonFilter;
    }

    public void setLedFilter(Predicate<LED> ledFilter) {
        this.ledFilter = ledFilter;
    }

    private <X> boolean shouldEnter(Predicate<X> predicate, X value, Predicate<X> secondPredicate) {
        if (predicate == null) {
            return secondPredicate.test(value);
        }
        return predicate.test(value) && secondPredicate.test(value);
    }

    @Override
    public boolean shouldEnter(Project project) {
        return shouldEnter(projectFilter, project, delegate::shouldEnter);
    }

    @Override
    public boolean shouldEnter(Area area) {
        return shouldEnter(areaFilter, area, delegate::shouldEnter);
    }

    @Override
    public boolean shouldEnter(DeviceGroup deviceGroup) {
        return shouldEnter(deviceGroupFilter, deviceGroup, delegate::shouldEnter);
    }

    @Override
    public boolean shouldEnter(Device device) {
        return shouldEnter(deviceFilter, device, delegate::shouldEnter);
    }

    @Override
    public boolean shouldEnter(Component component) {
        return shouldEnter(componentFilter, component, delegate::shouldEnter);
    }

    @Override
    public boolean shouldEnter(Button button) {
        return shouldEnter(buttonFilter, button, delegate::shouldEnter);
    }

    @Override
    public boolean shouldEnter(LED led) {
        return shouldEnter(ledFilter, led, delegate::shouldEnter);
    }

}
