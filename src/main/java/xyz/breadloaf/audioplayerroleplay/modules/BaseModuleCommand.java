package xyz.breadloaf.audioplayerroleplay.modules;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public abstract class BaseModuleCommand {
    public abstract String getModuleKey();

    @Command("remove")
    public void remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

        IUserFacingModule moduleInterface = ModuleManager.ENABLED_MODULES.get(getModuleKey());
        if (moduleInterface == null) {
            return;
        }

        ModuleKey<?> key = moduleInterface.getModuleKey();

        if (audioData.getModule(key).isPresent()) {
            audioData.removeModule(key);
            audioData.saveToItem(heldItem);
            context.getSource().sendSuccess(() -> Component.literal("Removed module ").append(moduleInterface.moduleName()).append(" from item").withStyle(ChatFormatting.GREEN), false);
        } else {
            context.getSource().sendFailure(Component.literal("Module not present on this item"));
        }
    }

    @Command("info")
    public void info(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        sendModuleGeneralInfo(context);

        sendModuleExactInfo(context, player);
    }

    private void sendModuleExactInfo(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            return;
        }

        AudioData audioData = AudioPlayerApi.instance().getAudioData(heldItem).orElse(null);

        if (audioData == null) {
            return;
        }

        IUserFacingModule moduleInterface = ModuleManager.ENABLED_MODULES.get(getModuleKey());
        if (moduleInterface == null) {
            return;
        }

        ModuleKey<?> key = moduleInterface.getModuleKey();

        if (key == null || audioData.getModule(key).isPresent()) {
            MutableComponent specificInfo = moduleInterface.itemSpecificInfo(heldItem, audioData);
            if (specificInfo != null) {
                context.getSource().sendSuccess(() -> Component.literal("Item Specific Info").withStyle(ChatFormatting.GREEN), false);
                context.getSource().sendSuccess(() -> specificInfo, false);
            }
        }
    }

    private void sendModuleGeneralInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        IUserFacingModule moduleInterface = ModuleManager.ENABLED_MODULES.get(getModuleKey());
        if (moduleInterface == null) { return; }

        context.getSource().sendSuccess(() -> moduleInterface.moduleName().withStyle(ChatFormatting.AQUA).append("\n").append(moduleInterface.generalUsageInfo()), false);
    }
}
