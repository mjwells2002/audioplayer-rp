package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlaylistFile {
    public HashMap<String, Playlist> playlists = new HashMap<>();

    public static class Playlist {
        public ArrayList<UUID> soundIds = new ArrayList<>();
        public String id;

        public Playlist(String id) {
            this.id = id;
        }

        public void append(UUID soundId) {
            soundIds.add(soundId);
        }

        public void remove(UUID soundId) {
            soundIds.remove(soundId);
        }
    }
}
