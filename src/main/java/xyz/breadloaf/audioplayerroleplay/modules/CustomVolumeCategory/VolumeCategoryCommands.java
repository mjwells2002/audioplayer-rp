package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xyz.breadloaf.audioplayerroleplay.modules.BaseModuleCommand;

@Command({"roleplay", "volume"})
public class VolumeCategoryCommands extends BaseModuleCommand {

    @Command("apply")
    public void apply(CommandContext<CommandSourceStack> context, @Name("Volume Category") CategoryManager.UserVolumeCategory volumeCategory) throws CommandSyntaxException {
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

        audioData.setModule(CustomVolumeCategory.CUSTOM_VOLUME_CATEGORY_MODULE, new VolumeCategoryModule(volumeCategory.id));
        audioData.saveToItem(heldItem);
        context.getSource().sendSuccess(() -> Component.literal("Custom volume category module applied, Category: ").append(CategoryManager.getChatComponentFor(volumeCategory.id).withStyle(ChatFormatting.AQUA)), false);
    }

    @Command("reload")
    public void reload(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        VolumeConfig.ConfigReloadResult reloadResult = CustomVolumeCategory.VOLUME_CATEGORIES.reloadWithResult();
        CategoryManager.reloadCategories();

        if (reloadResult == VolumeConfig.ConfigReloadResult.ERRORS) {
            context.getSource().sendFailure(Component.literal("Errors logged while reloading config!, please check server console for more information"));
        } else if (reloadResult == VolumeConfig.ConfigReloadResult.WARNINGS_LOGGED) {
            context.getSource().sendSuccess(() -> Component.literal("Reloaded categories from config; Warnings logged while reloading config, please check server console for more information"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Reloaded categories from config!"), false);
        }
    }

    @Override
    public String getModuleKey() {
        return CustomVolumeCategory.ID;
    }
}