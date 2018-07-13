package org.openhab.binding.lutron.internal.xml.visitor;

import java.net.URL;

import org.openhab.binding.lutron.internal.discovery.project.Project;
import org.openhab.binding.lutron.internal.xml.DbXmlInfoReader;

public class ProjectReader {

    private final Visitor visitor;

    public ProjectReader(Visitor visitor) {
        this.visitor = visitor;
    }

    public void read(URL url) {
        Project project = new DbXmlInfoReader().readFromXML(url);
        visitor.visit(project);
    }

}
