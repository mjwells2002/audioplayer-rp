package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xyz.breadloaf.audioplayerroleplay.modules.BaseModuleCommand;

import java.util.UUID;

import static xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedPlayback.RANDOM_PLAYBACK_MODULE;

@Command({"roleplay", "randomized"})
public class RandomizedPlaybackCommands extends BaseModuleCommand {

    @RequiresPermission("audioplayer_roleplay.test")
    @Command("enable")
    public void enable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

        audioData.setModule(RANDOM_PLAYBACK_MODULE, new RandomizedSoundModule(audioData.getSoundId()));
        audioData.saveToItem(heldItem);
        context.getSource().sendSuccess(() -> Component.literal("Enabled randomized playback for item"), false);
    }

    @RequiresPermission("audioplayer_roleplay.test")
    @Command("append")
    public void append(CommandContext<CommandSourceStack> context, UUID uuid) throws CommandSyntaxException {
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
        soundModule.addUUID(uuid);

        audioData.saveToItem(heldItem);

        context.getSource().sendSuccess(() -> Component.literal("Added sound to randomized playback for item"), false);
    }

    @Override
    public String getModuleKey() {
        return RandomizedPlayback.ID;
    }
}
