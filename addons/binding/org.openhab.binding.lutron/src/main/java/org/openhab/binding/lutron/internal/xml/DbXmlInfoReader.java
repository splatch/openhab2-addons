/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lutron.internal.xml;

import org.eclipse.smarthome.config.xml.util.XmlDocumentReader;
import org.openhab.binding.lutron.internal.discovery.project.Area;
import org.openhab.binding.lutron.internal.discovery.project.Button;
import org.openhab.binding.lutron.internal.discovery.project.Component;
import org.openhab.binding.lutron.internal.discovery.project.Device;
import org.openhab.binding.lutron.internal.discovery.project.DeviceGroup;
import org.openhab.binding.lutron.internal.discovery.project.LED;
import org.openhab.binding.lutron.internal.discovery.project.Output;
import org.openhab.binding.lutron.internal.discovery.project.Project;

import com.thoughtworks.xstream.XStream;

/**
 * The {@link DbXmlInfoReader} reads Lutron XML project files and converts them to {@link Project}
 * objects describing the device things contained within the Lutron system.
 *
 * @author Allan Tong - Initial contribution
 */
public class DbXmlInfoReader extends XmlDocumentReader<Project> {

    public DbXmlInfoReader() {
        super.setClassLoader(Project.class.getClassLoader());
    }

    @Override
    public void registerConverters(XStream xstream) {
    }

    @Override
    public void registerAliases(XStream xstream) {
        xstream.alias("Project", Project.class);
        xstream.aliasField("AppVer", Project.class, "appVersion");
        xstream.aliasField("XMLVer", Project.class, "xmlVersion");
        xstream.aliasField("Areas", Project.class, "areas");

        xstream.alias("Area", Area.class);
        xstream.aliasAttribute(Area.class, "name", "Name");
        xstream.aliasField("DeviceGroups", Area.class, "deviceNodes");
        xstream.aliasField("Outputs", Area.class, "outputs");
        xstream.aliasField("Areas", Area.class, "areas");

        xstream.alias("DeviceGroup", DeviceGroup.class);
        xstream.aliasAttribute(DeviceGroup.class, "name", "Name");
        xstream.aliasField("Devices", DeviceGroup.class, "devices");

        xstream.alias("Device", Device.class);
        xstream.aliasAttribute(Device.class, "name", "Name");
        xstream.aliasAttribute(Device.class, "integrationId", "IntegrationID");
        xstream.aliasAttribute(Device.class, "type", "DeviceType");
        xstream.aliasField("Components", Device.class, "components");

        xstream.alias("Component", Component.class);
        xstream.aliasAttribute(Component.class, "componentNumber", "ComponentNumber");
        xstream.aliasAttribute(Component.class, "type", "ComponentType");
        xstream.addImplicitCollection(Component.class, "components");

        xstream.alias("Output", Output.class);
        xstream.aliasAttribute(Output.class, "name", "Name");
        xstream.aliasAttribute(Output.class, "integrationId", "IntegrationID");
        xstream.aliasAttribute(Output.class, "type", "OutputType");

        xstream.alias("Button", Button.class);
        xstream.aliasAttribute(Button.class, "name", "Name");
        xstream.aliasAttribute(Button.class, "engraving", "Engraving");
        xstream.aliasAttribute(Button.class, "UUID", "UUID");
        xstream.aliasAttribute(Button.class, "reverseLogic", "ReverseLedLogic");
        xstream.aliasAttribute(Button.class, "programmingModelId", "ProgrammingModelID");

        xstream.alias("LED", LED.class);
        xstream.aliasAttribute(LED.class, "UUID", "UUID");
        xstream.aliasAttribute(LED.class, "reverseLogic", "ReverseLedLogic");
        xstream.aliasAttribute(LED.class, "programmingModelId", "ProgrammingModelID");

        // This reader is only interested in device thing information and does not read
        // everything contained in DbXmlInfo. Ignoring unknown elements also makes the
        // binding more tolerant of potential future changes to the XML schema.
        xstream.ignoreUnknownElements();
    }
}
