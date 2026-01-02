package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import com.google.gson.JsonObject;
import de.maxhenkel.audioplayer.api.data.AudioDataModule;
import net.minecraft.core.BlockPos;

public class RegionDataModule implements AudioDataModule {

    public Region region = new Region(0, 0, 0, 0, 0, 0);

    public RegionDataModule() {

    }

    public RegionDataModule(Region region) {
        this.region = region;
    }


    @Override
    public void load(JsonObject jsonObject) throws Exception {
        this.region = Region.fromJson(jsonObject.get("pos"));

        if (this.region == null) {
            throw new IllegalStateException("Failed to load Region Module from item, position values missing");
        }
    }

    @Override
    public void save(JsonObject jsonObject) throws Exception {
        jsonObject.add("pos", region.toJson());
    }


}
