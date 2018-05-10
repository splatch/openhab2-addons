package org.openhab.binding.lutron.internal.mapping;

import java.util.Optional;

class SimpleSwitch implements Switch {

    private final int action;
    private final Integer parameter;

    public SimpleSwitch(int action) {
        this(action, null);
    }

    public SimpleSwitch(int action, Integer parameter) {
        this.action = action;
        this.parameter = parameter;
    }

    @Override
    public int getAction() {
        return action;
    }

    @Override
    public Optional<Integer> getParameter() {
        return Optional.ofNullable(parameter);
    }

}
