package org.openhab.binding.lutron.internal.discovery.project;

import org.openhab.binding.lutron.internal.xml.visitor.Visitor;

public class LED extends AbstractComponentNode {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
