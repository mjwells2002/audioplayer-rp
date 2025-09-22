package xyz.breadloaf.audioplayerroleplay.audioplayer.events;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.AudioPlayerModule;
import de.maxhenkel.audioplayer.api.ChannelReference;
import de.maxhenkel.audioplayer.api.events.PlayEvent;
import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.ServerPlayer;
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel;
import net.minecraft.world.phys.Vec3;
import xyz.breadloaf.audioplayerroleplay.audioplayer.AudioplayerIntegration;
import xyz.breadloaf.audioplayerroleplay.permission.RoleplayPermissionManager;
import xyz.breadloaf.audioplayerroleplay.voicechat.RoleplayVoicechatPlugin;

import javax.xml.stream.Location;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static xyz.breadloaf.audioplayerroleplay.voicechat.RoleplayVoicechatPlugin.TEST_CATEGORY_ID;
import static xyz.breadloaf.audioplayerroleplay.voicechat.RoleplayVoicechatPlugin.voicechatServerApi;

public class StaticPlaybackHooks {
    public static void onPlayback(PlayEvent event) {
        AudioPlayerApi audioPlayerApi = AudioPlayerApi.instance();
        if (event.getModule(AudioplayerIntegration.STATIC_PLAYBACK_MODULE).isPresent()) {
            Optional<AudioPlayerModule> apModule = event.getModule(AudioPlayerModule.KEY);
            if (apModule.isEmpty()) {
                //TODO: fail gracefully
                throw new IllegalStateException("AudioPlayerModule is missing");
            }
            UUID soundUUID = apModule.get().getSoundId();
            Float range = apModule.get().getRange();

            if (range == null) {
                range = event.getDefaultDistance();
            }
            if (voicechatServerApi == null) {
                //TODO: fail gracefully
                throw new IllegalStateException("Voicechat API Missing");
            }
            StaticAudioChannel audioChannel = voicechatServerApi.createStaticAudioChannel(UUID.randomUUID());
            ChannelReference< StaticAudioChannel > channelReference = audioPlayerApi.playChannel(audioChannel,soundUUID,event.getPlayer());

            audioChannel.setCategory(null);

            Collection<ServerPlayer> players2 = voicechatServerApi.getPlayersInRange(voicechatServerApi.fromServerLevel(event.getLevel()), voicechatServerApi.createPosition(0,0,0), range);
            players2.forEach(serverPlayer -> {audioChannel.addTarget(voicechatServerApi.getConnectionOf(serverPlayer));});

            float finalRange = range;
            channelReference.setOnChannelUpdate((channel) -> {
                //TODO: this is bad, dont do this every update, also not called?
                Collection<ServerPlayer> players = voicechatServerApi.getPlayersInRange(voicechatServerApi.fromServerLevel(event.getLevel()), voicechatServerApi.createPosition(0,0,0), finalRange);
                players.forEach(serverPlayer -> {channel.addTarget(voicechatServerApi.getConnectionOf(serverPlayer));});

            });
            event.overrideChannel(channelReference);
        }

    }
}
