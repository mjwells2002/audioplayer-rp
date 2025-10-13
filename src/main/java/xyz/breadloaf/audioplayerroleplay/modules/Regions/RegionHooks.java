package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import de.maxhenkel.audioplayer.api.ChannelReference;
import de.maxhenkel.audioplayer.api.events.GetDistanceEvent;
import de.maxhenkel.audioplayer.api.events.PlayEvent;
import de.maxhenkel.audioplayer.api.events.PostPlayEvent;
import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
                System.out.println("WARNING: ignoring region for playback");
            }
        }
    }

    static void onGetDistance(GetDistanceEvent event) {
        RegionDataModule regionDataModule = event.getData().getModule(REGIONS_DATA_MODULE).orElse(null);
        if (regionDataModule != null) {
            Region region = regionDataModule.region;
            if (region.isNearbyEnoughToPlay(event.getPosition())) {
                //TODO: data that makes this automatic
                event.setDistance((float) (region.getMaxDistanceTo(event.getPosition()) + 1));
            }
        }
    }
}
