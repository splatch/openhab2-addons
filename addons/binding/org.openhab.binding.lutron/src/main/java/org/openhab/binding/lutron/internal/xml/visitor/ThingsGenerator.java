package org.openhab.binding.lutron.internal.xml.visitor;

import java.io.File;
import java.util.function.Predicate;

import org.openhab.binding.lutron.internal.discovery.project.Area;
import org.openhab.binding.lutron.internal.discovery.project.Button;
import org.openhab.binding.lutron.internal.discovery.project.Component;
import org.openhab.binding.lutron.internal.discovery.project.Device;
import org.openhab.binding.lutron.internal.discovery.project.DeviceGroup;
import org.openhab.binding.lutron.internal.discovery.project.DeviceType;
import org.openhab.binding.lutron.internal.discovery.project.LED;
import org.openhab.binding.lutron.internal.discovery.project.Project;

public class ThingsGenerator implements Visitor {

    public static void main(String[] args) throws Exception {
        FilteringVisitor delegate = new FilteringVisitor(new ThingsGenerator());
        delegate.setDeviceFilter(deviceType(DeviceType.MAIN_REPEATER).negate()
                .and(deviceType(DeviceType.SEETOUCH_KEYPAD).or(deviceType(DeviceType.HYBRID_SEETOUCH_KEYPAD))));

        ProjectReader reader = new ProjectReader(new TraversingVisitor(delegate));
        reader.read(new File("/Users/splatch/projects/DbXmlInfo.xml").toURI().toURL());

    }

    private static Predicate<Device> deviceType(DeviceType type) {
        return device -> {
            DeviceType deviceType = device.getDeviceType();
            return deviceType != null && deviceType.equals(type);
        };
    }

    @Override
    public void visit(Project project) {
        System.out.println(
                "Thing lutron:ipbridge:radiora2 [ ipAddress=\"192.168.1.150\", user=\"lutron\", password=\"integration\" ]");
        System.out.println();
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
        System.out.println(String.format("Thing lutron:keypad:%d (lutron:ipbridge:radiora2) [ integrationId=%d ]",
                device.getIntegrationId(), device.getIntegrationId()));
    }

    @Override
    public void leave(Device device) {

    }

    @Override
    public void visit(Component component) {
    }

    @Override
    public void visit(Button button) {
    }

    @Override
    public void visit(LED led) {

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
        return false;
    }

    @Override
    public boolean shouldEnter(Button button) {
        return false;
    }

    @Override
    public boolean shouldEnter(LED led) {
        return false;
    }

}
