package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.importer.AudioImportInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.importer.BulkImporterProvider;
import xyz.breadloaf.audioplayerroleplay.modules.AudioFile;
import xyz.breadloaf.audioplayerroleplay.modules.BaseModuleCommand;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedPlayback.RANDOM_PLAYBACK_MODULE;

@Command({"roleplay", "randomized"})
public class RandomizedPlaybackCommands extends BaseModuleCommand {

//    @RequiresPermission("audioplayer_roleplay.test")
//    @Command("enable")
//    public void enable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
//        ServerPlayer player = context.getSource().getPlayerOrException();
//        ItemStack heldItem = player.getMainHandItem();
//        if (heldItem.isEmpty()) {
//            context.getSource().sendFailure(Component.literal("You are not holding an item"));
//            return;
//        }
//        AudioData audioData = AudioPlayerApi.instance().getAudioData(heldItem).orElse(null);
//        if (audioData == null) {
//            context.getSource().sendFailure(Component.literal("Item has no audio data"));
//            return;
//        }
//
//        audioData.setModule(RANDOM_PLAYBACK_MODULE, new RandomizedSoundModule(audioData.getSoundId()));
//        audioData.saveToItem(heldItem);
//        context.getSource().sendSuccess(() -> Component.literal("Enabled randomized playback for item"), false);
//    }

    @RequiresPermission("audioplayer_roleplay.test")
    @Command("append")
    public void append(CommandContext<CommandSourceStack> context, @Name("id") AudioFile uuid) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            context.getSource().sendFailure(Component.literal("You are not holding an item"));
            return;
        }
        AudioData audioData = AudioPlayerApi.instance().getAudioData(heldItem).orElse(null);
        if (audioData == null) {
            context.getSource().sendFailure(Component.literal("Item has no audio data"));
            return;
        }

        RandomizedSoundModule soundModule = audioData.getModule(RANDOM_PLAYBACK_MODULE).orElse(null);

        if (soundModule == null) {
            soundModule = new RandomizedSoundModule(audioData.getSoundId());
            audioData.setModule(RANDOM_PLAYBACK_MODULE, soundModule);
        }
        soundModule.addUUID(uuid.soundID());

        audioData.saveToItem(heldItem);

        context.getSource().sendSuccess(() -> Component.literal("Added sound to randomized playback for item"), false);
    }

    @Command("set")
    public void append(CommandContext<CommandSourceStack> context, @Name("playlist_id") PlaylistFile.Playlist playlist) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            context.getSource().sendFailure(Component.literal("You are not holding an item"));
            return;
        }
        AudioData audioData = AudioPlayerApi.instance().getAudioData(heldItem).orElse(null);
        if (audioData == null) {
            context.getSource().sendFailure(Component.literal("Item has no audio data"));
            return;
        }



        RandomizedSoundModule soundModule = new RandomizedSoundModule(playlist.id);
        audioData.setModule(RANDOM_PLAYBACK_MODULE, soundModule);


        audioData.saveToItem(heldItem);

        context.getSource().sendSuccess(() -> Component.literal("Set randomized audio playlist"), false);
    }

    @Command("folder")
    public void folder(CommandContext<CommandSourceStack> context, @Name("folder") String folder, @Name("playlist_id") String pid) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Path importFolder = AudioPlayerApi.instance().getUploadFolder().resolve(folder);
        PlaylistFile.Playlist playlist = PlaylistManager.getPlaylist(pid);
        ArrayList<CompletableFuture<AudioImportInfo>> futures = new ArrayList<>();
        for (File file : importFolder.toFile().listFiles()) {
            futures.add(AudioPlayerApi.instance().importAudio(new BulkImporterProvider(file), player, false));
        }
        AudioPlayerRoleplayMod.WORKER.execute(() -> {
            int failedImport = 0;
            int impoprted = 0;
            for (CompletableFuture<AudioImportInfo> future : futures) {
                try {
                    AudioImportInfo x = future.join();
                    playlist.append(x.getAudioId());
                    impoprted++;
                } catch (Exception e) {
                    e.printStackTrace();
                    failedImport++;
                }
            }
            player.sendSystemMessage(Component.literal("Playlist import completed ")
                    .append("%d".formatted(impoprted)).append(" Files imported")
                    .append("%d".formatted(failedImport)).append(" Files failed to import"));

            PlaylistManager.save();
        });
        context.getSource().sendSuccess(() -> Component.literal("Added sound to randomized playback for item"), false);
    }

    @Override
    public String getModuleKey() {
        return RandomizedPlayback.ID;
    }
}
