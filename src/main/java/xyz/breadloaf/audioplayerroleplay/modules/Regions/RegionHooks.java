package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.ChannelReference;
import de.maxhenkel.audioplayer.api.events.GetDistanceEvent;
import de.maxhenkel.audioplayer.api.events.PlayEvent;
import de.maxhenkel.audioplayer.api.events.PostPlayEvent;
import de.maxhenkel.audioplayer.api.exceptions.ChannelAlreadyOverriddenException;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.network.chat.Component;

import java.util.UUID;

import static xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionsModule.REGIONS_DATA_MODULE;

public class RegionHooks {
    static void onPostPlay(PostPlayEvent event) {
        RegionDataModule regionDataModule = event.getData().getModule(REGIONS_DATA_MODULE).orElse(null);
        if (regionDataModule != null) {
            if (regionDataModule.isNearbyEnoughToPlay(event.getPosition())) {
                if (regionDataModule.regionMode != RegionMode.FALLOFF) {
                    ChannelReference<? extends AudioChannel> audioChannelChannelReference = event.getChannel();
                    audioChannelChannelReference.getChannel().setFilter(serverPlayer -> regionDataModule.containsPosition(serverPlayer.getPosition()));
                }
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
        if (regionDataModule != null && regionDataModule.regionMode == RegionMode.CLIP) {
            if (regionDataModule.isNearbyEnoughToPlay(event.getPosition())) {
                event.setDistance((float) (regionDataModule.getMaxDistanceTo(event.getPosition()) + 1));
            }
        }
    }

    public static void onPlay(PlayEvent event) {
        RegionDataModule regionDataModule = event.getData().getModule(REGIONS_DATA_MODULE).orElse(null);
        if (regionDataModule != null && regionDataModule.regionMode == RegionMode.FALLOFF) {
            HybridRegionChannel hybridRegionChannel = new HybridRegionChannel(UUID.randomUUID(),regionDataModule.regions,event.getDistance(),event.getCategory(), event.getLevel().dimension().identifier());
            ChannelReference<HybridRegionChannel> ref = AudioPlayerApi.instance().playChannel(hybridRegionChannel, event.getSoundId(), event.getPlayer());
            try {
                event.overrideChannel(ref);
            } catch (ChannelAlreadyOverriddenException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
