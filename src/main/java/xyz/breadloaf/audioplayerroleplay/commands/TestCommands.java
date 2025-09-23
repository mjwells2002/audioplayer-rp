package xyz.breadloaf.audioplayerroleplay.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.CustomVolumeCategory;
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.VolumeCategoryModule;
import xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlaybackModule;
import xyz.breadloaf.audioplayerroleplay.modules.IUserFacingModule;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleManager;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedSoundModule;
import static xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedPlayback.RANDOM_PLAYBACK_MODULE;
import static xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlayback.STATIC_PLAYBACK_MODULE;

import java.util.UUID;

@Command("roleplay")
public class TestCommands {

    @Command("set_static")
    public void setStatic(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

        audioData.setModule(STATIC_PLAYBACK_MODULE,new StaticPlaybackModule());
        audioData.saveToItem(heldItem);
        context.getSource().sendSuccess(() -> Component.literal("Test module applied"), false);
    }

    @RequiresPermission("audioplayer_roleplay.test")
    @Command("apply_test")
    public void applyTest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
        context.getSource().sendSuccess(() -> Component.literal("Test module applied"), false);
    }

    @RequiresPermission("audioplayer_roleplay.test")
    @Command("apply_test2")
    public void applyTest2(CommandContext<CommandSourceStack> context, UUID uuid) throws CommandSyntaxException {
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

        if (soundModule != null) {
            soundModule.addUUID(uuid);
        }

        audioData.saveToItem(heldItem);

        context.getSource().sendSuccess(() -> Component.literal("Test module applied"), false);
    }

    @Command("cvc")
    public void cvc(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

        audioData.setModule(CustomVolumeCategory.CUSTOM_VOLUME_CATEGORY_MODULE, new VolumeCategoryModule());
        audioData.saveToItem(heldItem);
        context.getSource().sendSuccess(() -> Component.literal("Test module applied"), false);
    }

    @Command("info")
    public void info(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

        context.getSource().sendSuccess(() -> Component.literal("Module Info for ").append(heldItem.getDisplayName()), false);

        for (IUserFacingModule moduleInterface : ModuleManager.MODULES.values()) {
            if (moduleInterface.hasModule(audioData)) {
                context.getSource().sendSuccess(() -> moduleInterface.moduleName().withStyle(ChatFormatting.AQUA).withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(moduleInterface.generalUsageInfo()))), false);
                MutableComponent specificInfo = moduleInterface.itemSpecificInfo(heldItem,audioData);
                if (specificInfo != null) {
                    context.getSource().sendSuccess(() -> specificInfo, false);
                }
            }
        }

    }
}
