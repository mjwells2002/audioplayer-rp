package xyz.breadloaf.audioplayerroleplay.importer;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.importer.AudioImportInfo;
import de.maxhenkel.audioplayer.api.importer.AudioImporter;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.io.IOUtils;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.UUID;



public class BulkImporterProvider implements AudioImporter {
    private final UUID soundId = UUID.randomUUID();
    private final File file;

    //TODO: add playlist support to randomized and make this take the playlist id, and provide a completable future somehow or otherwise provide a method to have a callback on playlist import finish

    public BulkImporterProvider(File file) {
        this.file = file;
    }

    public AudioImportInfo onPreprocess(@Nullable ServerPlayer player) throws Exception {
        if (Files.exists(this.file.toPath()) && Files.isRegularFile(this.file.toPath())) {
            long size = Files.size(this.file.toPath());
            if (size > AudioPlayerApi.instance().getMaxUploadSize()) {
                throw new NoSuchFileException("The file %s is too large".formatted(this.file.getName()));
            }
            return new AudioImportInfo(this.soundId, getFileNameFromPath(this.file.toPath()));
        } else {
            throw new NoSuchFileException("The file %s does not exist".formatted(this.file.getName()));
        }
    }

    @Nullable
    public static String getFileNameFromPath(Path path) {
        if (Files.isDirectory(path)) {
            return null;
        } else {
            String name = path.getFileName().toString();
            return name.isEmpty() ? null : name;
        }
    }

    public byte[] onProcess(@Nullable ServerPlayer player) throws Exception {
        return IOUtils.toByteArray(Files.newInputStream(this.file.toPath()));
    }

    public void onPostprocess(@Nullable ServerPlayer player) throws Exception {
        try {
            Files.delete(this.file.toPath());
            //player.sendSystemMessage(AudioPlayerApi.instance().createInfoMessage(soundId));
        } catch (Exception e) {
            AudioPlayerRoleplayMod.LOGGER.error("Failed to delete file {}", this.file, e);
        }

    }

    public String getHandlerName() {
        return "rp_bulkimporter";
    }
}