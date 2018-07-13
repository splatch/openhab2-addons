package org.openhab.binding.lutron.internal.xml.visitor;

import org.openhab.binding.lutron.internal.discovery.project.Area;
import org.openhab.binding.lutron.internal.discovery.project.Button;
import org.openhab.binding.lutron.internal.discovery.project.Component;
import org.openhab.binding.lutron.internal.discovery.project.Device;
import org.openhab.binding.lutron.internal.discovery.project.DeviceGroup;
import org.openhab.binding.lutron.internal.discovery.project.LED;
import org.openhab.binding.lutron.internal.discovery.project.Project;

public class TraversingVisitor implements Visitor {

    private final Visitor delegate;

    public TraversingVisitor(Visitor delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean shouldEnter(Project project) {
        return delegate.shouldEnter(project);
    }

    @Override
    public void visit(Project project) {
        if (shouldEnter(project)) {
            delegate.visit(project);
            project.getAreas().forEach(area -> area.accept(this));
            leave(project);
        }
    }

    @Override
    public void leave(Project project) {
        delegate.leave(project);
    }

    @Override
    public boolean shouldEnter(Area area) {
        return delegate.shouldEnter(area);
    }

    @Override
    public void visit(Area area) {
        if (shouldEnter(area)) {
            delegate.visit(area);

            area.getAreas().forEach(subArea -> subArea.accept(this));
            area.getDeviceNodes().forEach(deviceNode -> deviceNode.accept(this));
        }
    }

    @Override
    public boolean shouldEnter(DeviceGroup deviceGroup) {
        return delegate.shouldEnter(deviceGroup);
    }

    @Override
    public void visit(DeviceGroup deviceGroup) {
        if (shouldEnter(deviceGroup)) {
            delegate.visit(deviceGroup);

            deviceGroup.getDevices().forEach(device -> device.accept(this));
        }
    }

    @Override
    public boolean shouldEnter(Device device) {
        return delegate.shouldEnter(device);
    }

    @Override
    public void visit(Device device) {
        if (shouldEnter(device)) {
            delegate.visit(device);

            device.getComponents().forEach(component -> component.accept(this));
            leave(device);
        }
    }

    @Override
    public void leave(Device device) {
        delegate.leave(device);
    }

    @Override
    public boolean shouldEnter(Component component) {
        return delegate.shouldEnter(component);
    }

    @Override
    public void visit(Component component) {
        if (shouldEnter(component)) {
            delegate.visit(component);

            component.getComponents().forEach(subComponent -> subComponent.accept(this));
        }
    }

    @Override
    public boolean shouldEnter(Button button) {
        return delegate.shouldEnter(button);
    }

    @Override
    public void visit(Button button) {
        if (shouldEnter(button)) {
            delegate.visit(button);
        }
    }

    @Override
    public boolean shouldEnter(LED led) {
        return delegate.shouldEnter(led);
    }

    @Override
    public void visit(LED led) {
        if (shouldEnter(led)) {
            delegate.visit(led);
        }
    }

}
