package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import de.maxhenkel.audioplayer.api.ChannelReference;
import de.maxhenkel.audioplayer.api.events.GetDistanceEvent;
import de.maxhenkel.audioplayer.api.events.PostPlayEvent;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.network.chat.Component;

import static xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionsModule.REGIONS_DATA_MODULE;

public class RegionHooks {
    static void onPostPlay(PostPlayEvent event) {
        RegionDataModule regionDataModule = event.getData().getModule(REGIONS_DATA_MODULE).orElse(null);
        if (regionDataModule != null) {
            Region region = regionDataModule.region;
            if (region.isNearbyEnoughToPlay(event.getPosition())) {
                ChannelReference<? extends AudioChannel> audioChannelChannelReference = event.getChannel();
                audioChannelChannelReference.getChannel().setFilter(serverPlayer -> region.containsPosition(serverPlayer.getPosition()));
            } else {
                if (event.getPlayer() != null) {
                    event.getPlayer().sendOverlayMessage(Component.literal("Too far away from region to play!"));
                } else {
                    RegionsModule.LOGGER.warn("Ignoring region for playback, source is too far outside of region");
                }
                event.getChannel().stopPlaying();
            }
        }
    }

    static void onGetDistance(GetDistanceEvent event) {
        RegionDataModule regionDataModule = event.getData().getModule(REGIONS_DATA_MODULE).orElse(null);
        if (regionDataModule != null) {
            Region region = regionDataModule.region;
            if (region.isNearbyEnoughToPlay(event.getPosition())) {
                event.setDistance((float) (region.getMaxDistanceTo(event.getPosition()) + 1));
            } else {
                event.setDistance(0f);
            }
        }
    }
}
