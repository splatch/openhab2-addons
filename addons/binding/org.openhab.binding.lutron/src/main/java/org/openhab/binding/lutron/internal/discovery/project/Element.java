package org.openhab.binding.lutron.internal.discovery.project;

import org.openhab.binding.lutron.internal.xml.visitor.Visitor;

public interface Element {

    void accept(Visitor visitor);

}
