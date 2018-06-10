package org.openhab.binding.lutron.handler.profile;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.profiles.ProfileAdvisor;
import org.eclipse.smarthome.core.thing.profiles.ProfileTypeUID;
import org.eclipse.smarthome.core.thing.type.ChannelType;
import org.openhab.binding.lutron.LutronBindingConstants;
import org.osgi.service.component.annotations.Component;

@Component(service = { ProfileAdvisor.class })
public class LutronProfileAdvisor implements ProfileAdvisor {

    @Override
    public @Nullable ProfileTypeUID getSuggestedProfileTypeUID(@NonNull Channel channel, @Nullable String itemType) {
        // if (channelType.toString().contains("button")) {
        // return LutronBindingConstants.FOLLOW;
        // }
        return null;
    }

    @Override
    public @Nullable ProfileTypeUID getSuggestedProfileTypeUID(@NonNull ChannelType channelType,
            @Nullable String itemType) {
        if (channelType.toString().contains("button")) {
            return LutronBindingConstants.FOLLOW;
        }
        return null;
    }

}
