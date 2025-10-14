package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.maxhenkel.audioplayer.api.data.AudioDataModule;
import de.maxhenkel.voicechat.api.VolumeCategory;

public class VolumeCategoryModule implements AudioDataModule {
    String id;

    public VolumeCategoryModule() {
        this.id = null;
    }

    public VolumeCategoryModule(String id) {
        this.id = id;
    }

    @Override
    public void load(JsonObject jsonObject) throws Exception {
        JsonElement id = jsonObject.get("id");
        if (id != null && id.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = id.getAsJsonPrimitive();
            if (jsonPrimitive.isString()) {
                this.id = jsonPrimitive.getAsString();
            }
        }
    }

    @Override
    public void save(JsonObject jsonObject) throws Exception {
        jsonObject.addProperty("id",id);
    }
}
