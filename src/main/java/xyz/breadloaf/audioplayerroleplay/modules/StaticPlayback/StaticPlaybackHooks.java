package xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.ChannelReference;
import de.maxhenkel.audioplayer.api.events.PlayEvent;
import de.maxhenkel.audioplayer.api.exceptions.ChannelAlreadyOverriddenException;
import de.maxhenkel.voicechat.api.ServerPlayer;
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.UUID;

import static xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlayback.STATIC_PLAYBACK_MODULE;
import static xyz.breadloaf.audioplayerroleplay.voicechat.RoleplayVoicechatPlugin.voicechatServerApi;

public class StaticPlaybackHooks {
    public static void onPlayback(PlayEvent event) {
        AudioPlayerApi audioPlayerApi = AudioPlayerApi.instance();
        if (event.getData().getModule(STATIC_PLAYBACK_MODULE).isPresent()) {
            UUID soundUUID = event.getSoundId();
            float range = event.getDistance();

            if (voicechatServerApi == null) {
                //TODO: fail gracefully
                throw new IllegalStateException("Voicechat API Missing");
            }

            if (!event.isOverridden()) {
                System.out.println("Made Static");
                StaticAudioChannel audioChannel = voicechatServerApi.createStaticAudioChannel(UUID.randomUUID());
                if (audioChannel == null) {
                    //TODO: fail gracefully
                    throw new IllegalStateException("Creating Channel Failed");
                }
                ChannelReference<StaticAudioChannel> channelReference = audioPlayerApi.playChannel(audioChannel,soundUUID,event.getPlayer());

                audioChannel.setCategory(event.getCategory());

                Vec3 location = event.getPosition();

                channelReference.setOnChannelUpdate((channel) -> {
                    //TODO: this is bad, dont do this every update
                    channel.clearTargets();
                    Collection<ServerPlayer> players = voicechatServerApi.getPlayersInRange(voicechatServerApi.fromServerLevel(event.getLevel()), voicechatServerApi.createPosition(location.x,location.y,location.z), range);
                    players.forEach(serverPlayer -> {channel.addTarget(voicechatServerApi.getConnectionOf(serverPlayer));});
                });

                try {
                    event.overrideChannel(channelReference);
                } catch (ChannelAlreadyOverriddenException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
