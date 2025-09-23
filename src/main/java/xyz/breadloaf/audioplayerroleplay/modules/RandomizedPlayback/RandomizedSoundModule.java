package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback;

import de.maxhenkel.audioplayer.api.data.AudioDataModule;
import de.maxhenkel.audioplayer.api.data.DataAccessor;
import de.maxhenkel.audioplayer.api.data.DataModifier;

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
    public void load(DataAccessor dataAccessor) throws Exception {
        soundIds.clear();
        int count = dataAccessor.getInt("count");
        for (int i = 0; i < count; i++) {
            long low = dataAccessor.getLong(i + "L");
            long high = dataAccessor.getLong(i + "H");
            UUID uuid = new UUID(high,low);
            soundIds.add(uuid);
        }
    }

    @Override
    public void save(DataModifier dataModifier) throws Exception {
        int i = 0;
        for (UUID uuid : soundIds) {
            dataModifier.setLong(i + "L",uuid.getLeastSignificantBits());
            dataModifier.setLong(i + "H",uuid.getMostSignificantBits());
            i++;
        }
        dataModifier.setInt("count", i);
    }

    public UUID getUUID() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return soundIds.get(rng.nextInt(0,soundIds.size()));
    }
    public void addUUID(UUID uuid) {
        soundIds.add(uuid);
    }
}
