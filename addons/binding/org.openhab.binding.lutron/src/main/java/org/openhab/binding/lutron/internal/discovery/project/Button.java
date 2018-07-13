package org.openhab.binding.lutron.internal.discovery.project;

import org.openhab.binding.lutron.internal.xml.visitor.Visitor;

public class Button extends AbstractComponentNode {

    private String name;
    private String engraving; // label?

    public Button() {
        System.out.println("Create button");
    }

    public String getName() {
        return name;
    }

    public String getEngraving() {
        return engraving;
    }

    public String getLabel() {
        return engraving == null ? name : engraving;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
