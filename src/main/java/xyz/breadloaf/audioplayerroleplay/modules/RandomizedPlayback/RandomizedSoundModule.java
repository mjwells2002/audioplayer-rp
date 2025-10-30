package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.maxhenkel.audioplayer.api.data.AudioDataModule;


import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomizedSoundModule implements AudioDataModule {
    ArrayList<UUID> soundIds = new ArrayList<>();

    public RandomizedSoundModule() {

    }

    public RandomizedSoundModule(UUID u1) {
        soundIds.add(u1);
    }

    @Override
    public void load(JsonObject dataAccessor) throws Exception {
        soundIds.clear();
        JsonArray array = dataAccessor.getAsJsonArray("ids");
        for (JsonElement element : array) {
            if (element.isJsonArray()) {
                JsonArray uuid = element.getAsJsonArray();
                if (uuid.size() == 2) {
                    soundIds.add(new UUID(uuid.get(1).getAsLong(), uuid.get(0).getAsLong()));
                }
            }
        }
    }

    @Override
    public void save(JsonObject dataModifier) throws Exception {
        JsonArray array = new JsonArray();
        for (UUID uuid : soundIds) {
            JsonArray jsonUUID = new JsonArray();
            jsonUUID.add(uuid.getLeastSignificantBits());
            jsonUUID.add(uuid.getMostSignificantBits());
            array.add(jsonUUID);
        }
        dataModifier.add("ids", array);
    }

    public UUID getUUID() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return soundIds.get(rng.nextInt(0, soundIds.size()));
    }

    public void addUUID(UUID uuid) {
        soundIds.add(uuid);
    }

}
