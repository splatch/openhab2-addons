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

public class SitemapGenerator implements Visitor {

    public static void main(String[] args) throws Exception {
        FilteringVisitor delegate = new FilteringVisitor(new SitemapGenerator());
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

    private Device device;
    private Component component;

    @Override
    public void visit(Project project) {
        System.out.println("sitemap lutron label=\"Lutron\" {");
    }

    @Override
    public void leave(Project project) {
        // end sitemap
        System.out.println("}");
    }

    @Override
    public void visit(Area area) {
    }

    @Override
    public void visit(DeviceGroup deviceGroup) {
    }

    @Override
    public void visit(Device device) {
        System.out.println(String.format("\tFrame label=\"%s\"{", device.getName()));
        this.device = device;
    }

    @Override
    public void leave(Device device) {
        // end frame
        System.out.println("\t}");
        this.device = null;
    }

    @Override
    public void visit(Component component) {
        this.component = component;
    }

    @Override
    public void visit(Button button) {
        System.out.println("\t\tSwitch item=" + NameHelper.getItemName(device, component));
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
        return true;
    }

    @Override
    public boolean shouldEnter(Button button) {
        return true;
    }

    @Override
    public boolean shouldEnter(LED led) {
        return false;
    }

}
