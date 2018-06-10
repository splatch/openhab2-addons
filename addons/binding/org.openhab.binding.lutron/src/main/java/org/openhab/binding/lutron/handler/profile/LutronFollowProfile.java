package org.openhab.binding.lutron.handler.profile;

import org.eclipse.smarthome.core.thing.profiles.ProfileCallback;
import org.eclipse.smarthome.core.thing.profiles.ProfileContext;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeUID;
import org.eclipse.smarthome.core.thing.profiles.StateProfile;
import org.eclipse.smarthome.core.thing.profiles.SystemProfiles;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LutronFollowProfile implements StateProfile {

    private final Logger logger = LoggerFactory.getLogger(LutronFollowProfile.class);
    private final ProfileCallback callback;;
    private final ProfileContext context;

    public LutronFollowProfile(ProfileCallback callback, ProfileContext profileContext) {
        this.callback = callback;
        this.context = profileContext;
    }

    @Override
    public ProfileTypeUID getProfileTypeUID() {
        return SystemProfiles.FOLLOW;
    }

    @Override
    public void onStateUpdateFromItem(State state) {
        if (!(state instanceof Command)) {
            logger.debug("The given state {} could not be transformed to a command", state);
            return;
        }
        Command command = (Command) state;
        callback.handleCommand(command);
    }

    @Override
    public void onCommandFromHandler(Command command) {
        callback.sendCommand(command);
    }

    @Override
    public void onCommandFromItem(Command command) {
        // no-op
    }

    @Override
    public void onStateUpdateFromHandler(State state) {
        // no-op
    }

}
