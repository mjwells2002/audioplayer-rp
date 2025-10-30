package xyz.breadloaf.audioplayerroleplay.position;

import com.google.gson.Gson;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.Nullable;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PositionManager {
    static PositionFile POSITIONS = new PositionFile();

    @Nullable
    static Path filePath = null;

    public static void load() {
        Path path = AudioPlayerRoleplayMod.getModDataFolder();
        if (path != null) {
            path = path.resolve("saved_positions.json");
            filePath = path;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
                POSITIONS = new Gson().fromJson(bufferedReader, PositionFile.class);
            } catch (FileNotFoundException e) {
                save();
            } catch (IOException e) {
                AudioPlayerRoleplayMod.LOGGER.error("Error occurred while loading saved position data",e);
            }
        } else {
            throw new IllegalStateException("Position DB Attempted load before world load!");
        }
    }

    public static void save() {
        if (filePath != null) {
            AudioPlayerRoleplayMod.SAVE_WORKER.submit(() -> {
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                    if (!filePath.toFile().exists()) {
                        filePath.toFile().mkdirs();
                    }
                    bufferedWriter.write(new Gson().toJson(POSITIONS));
                    bufferedWriter.flush();
                } catch (IOException e) {
                    AudioPlayerRoleplayMod.LOGGER.error("Error occurred while saving position data",e);
                }
            });
        }
    }

    public static Set<String> getKeys() {
        return POSITIONS.id_to_location.keySet();
    }

    public static boolean exists(String id) {
        return POSITIONS.id_to_location.containsKey(id);
    }

    public static boolean create(String id, Vec3i pos) {
        if (exists(id)) {
            return false;
        }
        POSITIONS.id_to_location.put(id,pos);
        save();
        return true;
    }

    public static boolean update(String id, Vec3i pos) {
        if (!exists(id)) {
            return false;
        }
        POSITIONS.id_to_location.put(id,pos);
        save();
        return true;
    }
}
