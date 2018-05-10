package org.openhab.binding.lutron.internal.mapping;

import java.util.Optional;

/**
 * Representation of switch action in lutron.
 *
 * @author Łukasz Dywicki - initial contribution.
 */
public interface Switch {

    /**
     * Action code, as specified in integration protocol.
     *
     * @return Action code.
     */
    int getAction();

    /**
     * Optional arguments which might be necessary for action.
     *
     * @return Additional arguments for action.
     */
    Optional<Integer> getParameter();

}
