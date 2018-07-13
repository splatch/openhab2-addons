package org.openhab.binding.lutron.internal.discovery.project;

public interface ComponentNode extends Element {

    String getUUID();

    boolean isReverseLogic();

    String getProgrammingModelId();

}
