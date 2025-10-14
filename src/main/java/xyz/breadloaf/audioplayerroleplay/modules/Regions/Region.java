package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.maxhenkel.voicechat.api.Position;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Region {
    int minX;
    int minY;
    int minZ;
    int maxX;
    int maxY;
    int maxZ;

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

    public boolean containsPosition(Position position) {
        int x = (int) Math.floor(position.getX());
        int y = (int) Math.floor(position.getY());
        int z = (int) Math.floor(position.getZ());

        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
    }

    public boolean isNearbyEnoughToPlay(Vec3 pos) {
        if (RegionsModule.REGIONS_CONFIG.max_source_to_region_distance.get() <= 0) {
            return true;
        }
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
        return Math.sqrt(Math.max(pos.distanceToSqr(minX,minY,minZ),pos.distanceToSqr(maxX,maxY,maxZ)));
    }

    public JsonElement toJson() {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(minX);
        jsonArray.add(minY);
        jsonArray.add(minZ);
        jsonArray.add(maxX);
        jsonArray.add(maxY);
        jsonArray.add(maxZ);
        return jsonArray;
    }

    @Nullable
    public static Region fromJson(JsonElement array) {
        if (array.isJsonArray()) {
            JsonArray jsonArray = array.getAsJsonArray();
            if (jsonArray.size() == 6) {
                return new Region(jsonArray.get(0).getAsInt(),
                        jsonArray.get(1).getAsInt(),
                        jsonArray.get(2).getAsInt(),
                        jsonArray.get(3).getAsInt(),
                        jsonArray.get(4).getAsInt(),
                        jsonArray.get(5).getAsInt());
            }
        }
        return null;
    }
}