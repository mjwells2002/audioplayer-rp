package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.AudioFileMetadata;
import de.maxhenkel.audioplayer.api.importer.AudioImportInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.importer.BulkImporterProvider;
import xyz.breadloaf.audioplayerroleplay.modules.BaseModuleCommand;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleUtils;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.PlaylistFile;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.PlaylistManager;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedPlayback;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedSoundModule;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
    public void append(CommandContext<CommandSourceStack> context, @Name("id") AudioFileMetadata uuid) throws CommandSyntaxException {
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
        soundModule.addUUID(uuid.getAudioId());

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

        if (!ModuleUtils.isAudioItem(heldItem)) {
            context.getSource().sendFailure(Component.literal("Item cannot hold audio data"));
            return;
        }

        if (playlist.soundIds.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Cannot apply empty playlist to item!"));
            return;
        }

        AudioData audioData = AudioPlayerApi.instance().getAudioData(heldItem).orElse(null);
        if (audioData == null) {
            audioData = AudioPlayerApi.instance().createAudioData(AudioPlayerApi.instance().getAudioFileMetadata(playlist.soundIds.getFirst()).get());
        }

        RandomizedSoundModule soundModule = new RandomizedSoundModule(playlist.id);
        audioData.setModule(RANDOM_PLAYBACK_MODULE, soundModule);

        audioData.saveToItem(heldItem);

        context.getSource().sendSuccess(() -> Component.literal("Set randomized audio playlist"), false);
    }



    @Override
    public String getModuleKey() {
        return RandomizedPlayback.ID;
    }
}
