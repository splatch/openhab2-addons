package org.openhab.binding.lutron.internal.xml.visitor;

import org.openhab.binding.lutron.internal.discovery.project.Area;
import org.openhab.binding.lutron.internal.discovery.project.Button;
import org.openhab.binding.lutron.internal.discovery.project.Component;
import org.openhab.binding.lutron.internal.discovery.project.Device;
import org.openhab.binding.lutron.internal.discovery.project.DeviceGroup;
import org.openhab.binding.lutron.internal.discovery.project.LED;
import org.openhab.binding.lutron.internal.discovery.project.Project;

public interface Visitor {

    boolean shouldEnter(Project project);

    void visit(Project project);

    void leave(Project project);

    boolean shouldEnter(Area area);

    void visit(Area area);

    boolean shouldEnter(DeviceGroup deviceGroup);

    void visit(DeviceGroup deviceGroup);

    boolean shouldEnter(Device device);

    void visit(Device device);

    void leave(Device device);

    boolean shouldEnter(Component component);

    void visit(Component component);

    boolean shouldEnter(Button button);

    void visit(Button button);

    boolean shouldEnter(LED led);

    void visit(LED led);

}
