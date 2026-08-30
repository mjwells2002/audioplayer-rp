package xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.ChannelReference;
import de.maxhenkel.audioplayer.api.events.GetDistanceEvent;
import de.maxhenkel.audioplayer.api.events.PlayEvent;
import de.maxhenkel.audioplayer.api.events.PostPlayEvent;
import de.maxhenkel.audioplayer.api.exceptions.ChannelAlreadyOverriddenException;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.*;

import java.util.UUID;

import static xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionsModule.REGIONS_DATA_MODULE;

public class LocationHooks {
    public static void onPlay(PlayEvent event) {
        VirtualLocationModule locationModule = event.getData().getModule(VirtualLocations.VIRTUAL_LOCATION_PLAYBACK_MODULE).orElse(null);
        if (locationModule != null &&
                locationModule.position.vec3i() != null && locationModule.position.vec3() != null
        ) {
            if (VirtualLocations.VLOCATION_CONFIG.max_source_to_location_distance.get() >= 0 &&
                VirtualLocations.VLOCATION_CONFIG.max_source_to_location_distance.get() < event.getPosition().distanceTo(locationModule.position.vec3())) {
                if (event.getPlayer() != null) {
                    event.getPlayer().sendOverlayMessage(Component.literal("Virtual location too far away!"));
                } else {
                    AudioPlayerRoleplayMod.LOGGER.warn("Virtual location too far away from source at " + event.getPosition().toString());
                }
            }
            event.setPosition(locationModule.position.vec3());
        }
    }
}
