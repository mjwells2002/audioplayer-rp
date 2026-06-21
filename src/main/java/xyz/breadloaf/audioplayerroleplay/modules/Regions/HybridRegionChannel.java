package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import de.maxhenkel.voicechat.api.ServerPlayer;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import de.maxhenkel.voicechat.api.packets.LocationalSoundPacket;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import de.maxhenkel.voicechat.api.packets.StaticSoundPacket;
import org.jetbrains.annotations.Nullable;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.voicechat.RoleplayVoicechatPlugin;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Predicate;

public class HybridRegionChannel implements AudioChannel {
    private final UUID id;
    private final Region region;
    private final float distance;
    private final LocationalSoundPacket.Builder<?> locationalBuilder;
    private final StaticSoundPacket.Builder<?> staticBuilder;
    private String category;
    private long seqno;
    
    
    public HybridRegionChannel(UUID id, Region region, float distance, String category) {
        if (RoleplayVoicechatPlugin.voicechatServerApi == null) {
            throw new IllegalStateException("THE UNIVERSE EXPLODED");
        }
        VoicechatServerApi serverApi = RoleplayVoicechatPlugin.voicechatServerApi;

        this.id = id;
        this.region = region;
        this.distance = distance;
        this.category = category;
        this.locationalBuilder = serverApi.createPacket().locationalSoundPacketBuilder();
        this.staticBuilder = serverApi.createPacket().staticSoundPacketBuilder();
        locationalBuilder.channelId(this.id);
        locationalBuilder.sender(this.id);
        locationalBuilder.category(this.category);
        locationalBuilder.distance(this.distance);
        staticBuilder.channelId(this.id);
        staticBuilder.sender(this.id);
        staticBuilder.category(this.category);
    }

    @Override
    public void send(byte[] opusData) {
        if (RoleplayVoicechatPlugin.voicechatServerApi == null) {
            throw new IllegalStateException("THE UNIVERSE EXPLODED");
        }
        VoicechatServerApi serverApi = RoleplayVoicechatPlugin.voicechatServerApi;

        locationalBuilder.opusEncodedData(opusData);
        locationalBuilder.sequenceNumber(seqno);
        staticBuilder.opusEncodedData(opusData);
        staticBuilder.sequenceNumber(seqno);
        seqno++;

        ArrayList<Region.PlayerPoint> playerPoints = this.region.getPlayersWithin((int) Math.ceil(this.distance));

        if (playerPoints != null) {
            StaticSoundPacket packet = staticBuilder.build();
            for (Region.PlayerPoint point : playerPoints) {
                if (point.isInside()) {
                    serverApi.sendStaticSoundPacketTo(point.connection(), packet);
                } else {
                    locationalBuilder.position(point.sourcePos());
                    LocationalSoundPacket locationalSoundPacket = locationalBuilder.build();
                    serverApi.sendLocationalSoundPacketTo(point.connection(), locationalSoundPacket);
                }
            }
        }
    }

    @Override
    public void send(MicrophonePacket packet) {
        AudioPlayerRoleplayMod.LOGGER.debug("Send MicPaket not supported on HybridRegionChannel");
    }

    @Override
    public void setFilter(Predicate<ServerPlayer> filter) {
        AudioPlayerRoleplayMod.LOGGER.debug("Filter is not supported on HybridRegionChannel");
    }

    @Override
    public void flush() {

    }

    @Override
    public boolean isClosed() {
        return RoleplayVoicechatPlugin.voicechatServerApi != null;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public @Nullable String getCategory() {
        return this.category;
    }

    @Override
    public void setCategory(@Nullable String category) {
        this.category = category;
        this.locationalBuilder.category(this.category);
        this.staticBuilder.category(this.category);
    }
}
