package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.voicechat.api.VolumeCategory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xyz.breadloaf.audioplayerroleplay.modules.BaseModuleCommand;

import java.util.Map;

@Command({"roleplay", "volume"})
public class VolumeCategoryCommands extends BaseModuleCommand {

    @Command("apply")
    public void cvc(CommandContext<CommandSourceStack> context, CategoryManager.UserVolumeCategory volumeCategory) throws CommandSyntaxException {
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
        context.getSource().sendSuccess(() -> Component.literal("Test module applied"), false);
    }

    @Command("reload")
    public void reload(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomVolumeCategory.VOLUME_CATEGORIES.reload();

        for (Map.Entry<String, VolumeConfig.VolumeCategory> entry : CustomVolumeCategory.VOLUME_CATEGORIES.volumeCategories.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().name.get());
            System.out.println(entry.getValue().icon.get());
            System.out.println(entry.getValue().description.get());
        }

        CategoryManager.reloadCategories();
    }

    @Override
    public String getModuleKey() {
        return CustomVolumeCategory.ID;
    }
}