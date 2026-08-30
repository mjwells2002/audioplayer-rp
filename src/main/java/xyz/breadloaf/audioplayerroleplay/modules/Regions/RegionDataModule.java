package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.maxhenkel.audioplayer.api.data.AudioDataModule;
import de.maxhenkel.voicechat.api.Position;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class RegionDataModule implements AudioDataModule {
    public ArrayList<Region> regions = new ArrayList<Region>();
    public RegionMode regionMode = RegionMode.CLIP;

    public RegionDataModule() {

    }

    public RegionDataModule(Region region, RegionMode mode) {
        this.regions.add(region);
        this.regionMode = mode;
    }

    public void extend(Region region) {
        this.regions.add(region);
    }

    public boolean shrink(Region region) {
        return this.regions.removeIf(region1 -> Objects.equals(region1.id, region.id));
    }

    public boolean isNearbyEnoughToPlay(Vec3 pos) {
        boolean ret = false;
        for (Region region : regions) {
            ret |= region.isNearbyEnoughToPlay(pos);
        }
        return ret;
    }

    public boolean containsPosition(Position position) {
        boolean ret = false;
        for (Region region : regions) {
            ret |= region.containsPosition(position);
        }
        return ret;
    }

    public double getMaxDistanceTo(Vec3 position) {
        double ret = 0;
        for (Region region : regions) {
            double a = region.getMaxDistanceTo(position);
            if (a > ret) {
                ret = a;
            }
        }
        return ret;
    }

    @Override
    public void load(JsonObject jsonObject) throws Exception {
        Region migration = Region.fromJson(jsonObject.get("pos"));
        if (migration != null) {
            this.regions.add(migration);
        } else {
            JsonElement pos = jsonObject.get("pos");
            if (pos.isJsonArray()) {
                JsonArray arr = pos.getAsJsonArray();
                for (int i = 0; i<arr.size(); i++) {
                    Region region = Region.fromJson(arr.get(i));
                    if (region == null) {
                        AudioPlayerRoleplayMod.LOGGER.warn("failed to load part of region");
                    } else {
                        regions.add(region);
                    }
                }
            }
        }

        JsonElement regionModeElement = jsonObject.get("mode");
        if (regionModeElement != null) {
            JsonPrimitive jsonPrimitive = regionModeElement.getAsJsonPrimitive();
            this.regionMode = RegionMode.valueOf(jsonPrimitive.getAsString());
        }

        if (this.regions.isEmpty()) {
            throw new IllegalStateException("Failed to load Region Module from item, position values missing");
        }
    }

    @Override
    public void save(JsonObject jsonObject) throws Exception {
        JsonArray pos = new JsonArray();
        for (Region region : regions) {
            pos.add(region.toJson());
        }
        jsonObject.add("pos", pos);
        jsonObject.add("mode", new JsonPrimitive(regionMode.name()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("mode: ");
        sb.append(this.regionMode.toString());
        sb.append("\n");
        for (Region region : regions) {
            sb.append("region: ");
            sb.append(region.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
