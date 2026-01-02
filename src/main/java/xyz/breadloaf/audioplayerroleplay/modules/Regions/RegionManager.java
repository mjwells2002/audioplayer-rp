package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import com.google.gson.Gson;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;

public class RegionManager {
    public static @Nullable RegionFile REGIONS = new RegionFile();
    @Nullable
    static Path filePath = null;

    public static void load() {
        Path path = AudioPlayerRoleplayMod.getModDataFolder();
        if (path != null) {
            path = path.resolve("regions.json");
            filePath = path;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
                REGIONS = new Gson().fromJson(bufferedReader, RegionFile.class);
            } catch (FileNotFoundException e) {
                save();
            } catch (IOException e) {
                AudioPlayerRoleplayMod.LOGGER.error("Error occurred while loading saved position data", e);
            }
        } else {
            throw new IllegalStateException("Position DB Attempted load before world load!");
        }
    }

    public static void save() {
        if (filePath != null) {
            AudioPlayerRoleplayMod.SAVE_WORKER.submit(() -> {
                if (!filePath.toFile().exists()) {
                    filePath.getParent().toFile().mkdirs();
                }
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                    bufferedWriter.write(new Gson().toJson(REGIONS));
                    bufferedWriter.flush();
                } catch (IOException e) {
                    AudioPlayerRoleplayMod.LOGGER.error("Error occurred while saving position data", e);
                }
            });
        }
    }

    public static void setNamedRegion(String id, BlockPos pos1, BlockPos pos2) {
        if (REGIONS != null) {
            Region region = new Region(pos1,pos2);
            int[] regionData = new int[6];
            regionData[0] = region.minX;
            regionData[1] = region.minY;
            regionData[2] = region.minZ;
            regionData[3] = region.maxX;
            regionData[4] = region.maxY;
            regionData[5] = region.maxZ;
            REGIONS.id_to_minmax.put(id, regionData);
            save();
        } else {
            throw new IllegalStateException("Region initialization not complete");
        }
    }
}
