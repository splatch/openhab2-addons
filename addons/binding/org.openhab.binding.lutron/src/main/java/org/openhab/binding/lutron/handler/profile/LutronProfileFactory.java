package org.openhab.binding.lutron.handler.profile;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.profiles.Profile;
import org.eclipse.smarthome.core.thing.profiles.ProfileCallback;
import org.eclipse.smarthome.core.thing.profiles.ProfileContext;
import org.eclipse.smarthome.core.thing.profiles.ProfileFactory;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeUID;
import org.openhab.binding.lutron.LutronBindingConstants;
import org.osgi.service.component.annotations.Component;

@Component(service = { ProfileFactory.class })
public class LutronProfileFactory implements ProfileFactory {

    @Override
    public @Nullable Profile createProfile(@NonNull ProfileTypeUID profileTypeUID, @NonNull ProfileCallback callback,
            @NonNull ProfileContext profileContext) {
        if (LutronBindingConstants.FOLLOW.equals(profileTypeUID)) {
            return new LutronFollowProfile(callback, profileContext);
        }

        return null;
    }

    @Override
    public @NonNull Collection<@NonNull ProfileTypeUID> getSupportedProfileTypeUIDs() {
        return Arrays.asList(LutronBindingConstants.FOLLOW);
    }

}