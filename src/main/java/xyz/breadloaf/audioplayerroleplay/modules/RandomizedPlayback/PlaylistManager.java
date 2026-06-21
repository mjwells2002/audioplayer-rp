package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback;

import com.google.gson.Gson;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.Region;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionFile;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

public class PlaylistManager {
    public static @Nullable PlaylistFile PLAYLISTS = new PlaylistFile();
    @Nullable
    static Path filePath = null;

    public static void load() {
        Path path = AudioPlayerRoleplayMod.getModDataFolder();
        if (path != null) {
            path = path.resolve("playlists.json");
            filePath = path;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
                PLAYLISTS = new Gson().fromJson(bufferedReader, PlaylistFile.class);
            } catch (FileNotFoundException e) {
                save();
            } catch (IOException e) {
                AudioPlayerRoleplayMod.LOGGER.error("Error occurred while loading saved playlist data", e);
            }
        } else {
            throw new IllegalStateException("Playlist Attempted load before world load!");
        }
    }

    public static void save() {
        if (filePath != null) {
            AudioPlayerRoleplayMod.SAVE_WORKER.submit(() -> {
                if (!filePath.toFile().exists()) {
                    filePath.getParent().toFile().mkdirs();
                }
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                    bufferedWriter.write(new Gson().toJson(PLAYLISTS));
                    bufferedWriter.flush();
                } catch (IOException e) {
                    AudioPlayerRoleplayMod.LOGGER.error("Error occurred while saving playlist data", e);
                }
            });
        }
    }

    public static PlaylistFile.Playlist getPlaylist(String id) {
        if (PLAYLISTS != null) {
            PlaylistFile.Playlist playlist = PLAYLISTS.playlists.computeIfAbsent(id, (_) -> new PlaylistFile.Playlist(id));
            playlist.id = id;
            return playlist;
        } else {
            throw new IllegalStateException("Playlist initialization not complete");
        }
    }
}
