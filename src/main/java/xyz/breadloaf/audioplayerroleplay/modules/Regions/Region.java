package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import de.maxhenkel.voicechat.api.Position;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Region {
    int minX;
    int minY;
    int minZ;
    int maxX;
    int maxY;
    int maxZ;
    @Nullable String id = null;

    Region(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public Region(BlockPos pos1, BlockPos pos2) {
        this.minX = Math.min(pos1.getX(), pos2.getX());
        this.minY = Math.min(pos1.getY(), pos2.getY());
        this.minZ = Math.min(pos1.getZ(), pos2.getZ());

        this.maxX = Math.max(pos1.getX(), pos2.getX());
        this.maxY = Math.max(pos1.getY(), pos2.getY());
        this.maxZ = Math.max(pos1.getZ(), pos2.getZ());
    }

    public Region(@NotNull String id) {
        this.id = id;
    }

    private void updatePosition() {
        if (this.id == null) {
            return;
        }
        if (RegionManager.REGIONS == null) {
            RegionsModule.LOGGER.warn("Region data for {} is missing, defaulting to 0,0,0 -> 0,0,0", this.id);
            this.minX = 0;
            this.minY = 0;
            this.minZ = 0;
            this.maxX = 0;
            this.maxY = 0;
            this.maxZ = 0;
            return;
        }
        int[] data = RegionManager.REGIONS.id_to_minmax.get(this.id);
        if (data == null || data.length != 6) {
            RegionsModule.LOGGER.warn("Region data for {} is missing, defaulting to 0,0,0 -> 0,0,0", this.id);
            this.minX = 0;
            this.minY = 0;
            this.minZ = 0;
            this.maxX = 0;
            this.maxY = 0;
            this.maxZ = 0;
            return;
        }
        this.minX = data[0];
        this.minY = data[1];
        this.minZ = data[2];
        this.maxX = data[3];
        this.maxY = data[4];
        this.maxZ = data[5];
    }

    public boolean containsPosition(Position position) {
        updatePosition();
        int x = (int) Math.floor(position.getX());
        int y = (int) Math.floor(position.getY());
        int z = (int) Math.floor(position.getZ());

        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
    }

    public boolean isNearbyEnoughToPlay(Vec3 pos) {
        if (RegionsModule.REGIONS_CONFIG.max_source_to_region_distance.get() <= 0) {
            return true;
        }
        updatePosition();
        int tolerance = RegionsModule.REGIONS_CONFIG.max_source_to_region_distance.get();
        int x = (int) Math.floor(pos.x);
        int y = (int) Math.floor(pos.y);
        int z = (int) Math.floor(pos.z);
        int minX = this.minX - tolerance;
        int minY = this.minY - tolerance;
        int minZ = this.minZ - tolerance;
        int maxX = this.maxX + tolerance;
        int maxY = this.maxY + tolerance;
        int maxZ = this.maxZ + tolerance;
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public double getMaxDistanceTo(Vec3 pos) {
        updatePosition();
        return Math.sqrt(Math.max(pos.distanceToSqr(minX, minY, minZ), pos.distanceToSqr(maxX, maxY, maxZ)));
    }

    public JsonElement toJson() {
        if (this.id == null) {
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(minX);
            jsonArray.add(minY);
            jsonArray.add(minZ);
            jsonArray.add(maxX);
            jsonArray.add(maxY);
            jsonArray.add(maxZ);
            return jsonArray;
        } else {
            return new JsonPrimitive(this.id);
        }
    }

    public MutableComponent chatComponent() {
        updatePosition();
        MutableComponent component = Component.empty();
        if (this.id != null) {
            component.append(Component.literal("ID: "));
            component.append(Component.literal(this.id)
                    .withStyle(ChatFormatting.AQUA));
            component.append(Component.literal(" "));
            component.append(Component.literal("[Edit]")
                            .withStyle(ChatFormatting.GREEN))
                            .withStyle(style -> style.withClickEvent(
                                new ClickEvent.SuggestCommand(("/roleplay regions named set %s %d %d %d %d %d %d")
                                        .formatted(this.id, minX, minY, minZ, maxX, maxY, maxZ))));
            component.append(Component.literal("\n"));
        }
        component.append(Component.literal("Bounds: "));
        component.append(Component.literal("[" + minX + ", " + minY + ", " + minZ + "] -> [" + maxX + ", " + maxY + ", " + maxZ + "]")
                .withStyle(ChatFormatting.AQUA));
        component.append(Component.literal(" "));
        component.append(Component.literal("[Edit]")
                .withStyle(ChatFormatting.GREEN)
                .withStyle(style -> style.withClickEvent(
                        new ClickEvent.SuggestCommand(
                                "/roleplay regions apply %d %d %d %d %d %d".formatted(minX, minY, minZ, maxX, maxY, maxZ)))));
        return component;
    }

    @Nullable
    public static Region fromJson(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            if (jsonArray.size() == 6) {
                return new Region(jsonArray.get(0).getAsInt(),
                        jsonArray.get(1).getAsInt(),
                        jsonArray.get(2).getAsInt(),
                        jsonArray.get(3).getAsInt(),
                        jsonArray.get(4).getAsInt(),
                        jsonArray.get(5).getAsInt());
            }
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            return new Region(jsonPrimitive.getAsString());
        }
        return null;
    }
}