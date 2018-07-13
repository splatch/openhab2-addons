package org.openhab.binding.lutron.internal.discovery.project;

public abstract class AbstractComponentNode implements ComponentNode {

    private String UUID;
    private boolean reverseLogic;
    private String programmingModelId;

    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public boolean isReverseLogic() {
        return reverseLogic;
    }

    @Override
    public String getProgrammingModelId() {
        return programmingModelId;
    }

}
