package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.maxhenkel.audioplayer.api.data.AudioDataModule;
import org.jetbrains.annotations.Nullable;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.PlaylistManager.*;

public class RandomizedSoundModule implements AudioDataModule {

    @Nullable
    private String id;
    private final ArrayList<UUID> soundIds = new ArrayList<>();

    public RandomizedSoundModule() {
    }

    public RandomizedSoundModule(UUID u1) {
        soundIds.add(u1);
    }

    public RandomizedSoundModule(String id) {
        this.id = id;
    }

    public RandomizedSoundModule(List<UUID> uuids) {
        soundIds.addAll(uuids);
    }

    @Nullable
    public String getId() {
        return this.id;
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
        if (dataAccessor.get("id") != null) {
            this.id = dataAccessor.get("id").getAsString();
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
        if (this.id != null) {
            dataModifier.add("id",new JsonPrimitive(this.id));
        }
    }

    public UUID getUUID() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        if (this.id != null) {
            ArrayList<UUID> soundIds = getPlaylist(this.id).soundIds;
            return soundIds.get(rng.nextInt(0, soundIds.size()));
        }
        return this.soundIds.get(rng.nextInt(0, soundIds.size()));
    }

    public void addUUID(UUID uuid) {
        if (this.id != null) {
            getPlaylist(this.id).soundIds.add(uuid);
            PlaylistManager.save();
        }
        soundIds.add(uuid);
    }

    public ArrayList<UUID> getSoundIds() {
        if (this.id != null) {
            return getPlaylist(this.id).soundIds;
        }
        return soundIds;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id: ");
        sb.append(this.id);
        sb.append("\n");
        for (UUID id : soundIds) {
            sb.append("sound_id: ");
            sb.append(id);
            sb.append("\n");
        }
        return sb.toString();
    }
}
