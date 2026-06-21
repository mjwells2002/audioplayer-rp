package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback;

import de.maxhenkel.audioplayer.api.data.AudioFileMetadata;
import de.maxhenkel.audioplayer.api.data.AudioFileOwner;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record DummyAudioFileMetadata(UUID audioId, String fileName) implements AudioFileMetadata {
    @Override
    public UUID getAudioId() {
        return this.audioId;
    }

    @Override
    public @Nullable String getFileName() {
        return this.fileName;
    }

    @Override
    public @Nullable Float getVolume() {
        return null;
    }

    @Override
    public @Nullable Long getCreated() {
        return null;
    }

    @Override
    public @Nullable AudioFileOwner getOwner() {
        return null;
    }
}
