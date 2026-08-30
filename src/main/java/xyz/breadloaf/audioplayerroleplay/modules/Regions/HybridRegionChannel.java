package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.ServerPlayer;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import de.maxhenkel.voicechat.api.packets.LocationalSoundPacket;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import de.maxhenkel.voicechat.api.packets.StaticSoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.voicechat.RoleplayVoicechatPlugin;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HybridRegionChannel implements AudioChannel {
    private final UUID id;
    private final ArrayList<Region> regions;
    private final float distance;
    private final LocationalSoundPacket.Builder<?> locationalBuilder;
    private final StaticSoundPacket.Builder<?> staticBuilder;
    private String category;
    private long seqno;
    private Identifier dimension;
    
    public HybridRegionChannel(UUID id, ArrayList<Region> region, float distance, String category, Identifier dimension) {
        if (RoleplayVoicechatPlugin.voicechatServerApi == null) {
            throw new IllegalStateException("THE UNIVERSE EXPLODED");
        }
        VoicechatServerApi serverApi = RoleplayVoicechatPlugin.voicechatServerApi;

        this.id = id;
        this.regions = region;
        this.distance = distance;
        this.category = category;
        this.dimension = dimension;
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

        Map<UUID, List<Region.PlayerPoint>> pointsByConnection = this.regions.stream()
                .map(x -> x.getPlayersWithin((int) Math.ceil(this.distance),this.dimension))
                .filter(Objects::nonNull).flatMap(ArrayList::stream)
                .collect(Collectors.groupingBy(x -> x.connection().getPlayer().getUuid()));

        for (Map.Entry<UUID, List<Region.PlayerPoint>> entry : pointsByConnection.entrySet()) {
            boolean isInside = false;
            StaticSoundPacket packet = staticBuilder.build();
            Vec3 srcpos = null;

            for (Region.PlayerPoint point : entry.getValue()) {
                isInside |= point.isInside();
                Vec3 pos = new Vec3(point.sourcePos().getX(), point.sourcePos().getY(), point.sourcePos().getZ());
                Position playerPosRaw = point.connection().getPlayer().getPosition();
                Vec3 playerPos = new Vec3(playerPosRaw.getX(),playerPosRaw.getY(),playerPosRaw.getZ());
                if (srcpos == null) {
                    srcpos = pos;
                }
                else if (playerPos.distanceTo(srcpos) > playerPos.distanceTo(pos)) {
                    srcpos = pos;
                }
            }

            if (isInside) {
                serverApi.sendStaticSoundPacketTo(entry.getValue().getFirst().connection(), packet);
            } else if (srcpos != null) {
                locationalBuilder.position(serverApi.createPosition(srcpos.x,srcpos.y,srcpos.z));
                LocationalSoundPacket locationalSoundPacket = locationalBuilder.build();
                serverApi.sendLocationalSoundPacketTo(entry.getValue().getFirst().connection(), locationalSoundPacket);
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
