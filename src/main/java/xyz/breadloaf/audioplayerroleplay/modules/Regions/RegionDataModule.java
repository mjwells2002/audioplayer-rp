package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.maxhenkel.audioplayer.api.data.AudioDataModule;
import net.minecraft.core.BlockPos;

public class RegionDataModule implements AudioDataModule {

    public Region region = new Region(0, 0, 0, 0, 0, 0);
    public RegionMode regionMode = RegionMode.CLIP;

    public RegionDataModule() {

    }

    public RegionDataModule(Region region, RegionMode mode) {
        this.region = region;
        this.regionMode = mode;
    }


    @Override
    public void load(JsonObject jsonObject) throws Exception {
        this.region = Region.fromJson(jsonObject.get("pos"));

        JsonElement regionModeElement = jsonObject.get("mode");
        if (regionModeElement != null) {
            JsonPrimitive jsonPrimitive = regionModeElement.getAsJsonPrimitive();
            this.regionMode = RegionMode.valueOf(jsonPrimitive.getAsString());
        }

        if (this.region == null) {
            throw new IllegalStateException("Failed to load Region Module from item, position values missing");
        }
    }

    @Override
    public void save(JsonObject jsonObject) throws Exception {
        jsonObject.add("pos", region.toJson());
        jsonObject.add("mode", new JsonPrimitive(regionMode.name()));
    }


}
