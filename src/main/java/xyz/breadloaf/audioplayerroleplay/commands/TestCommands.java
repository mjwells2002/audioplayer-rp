package xyz.breadloaf.audioplayerroleplay.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.CustomVolumeCategory;
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.VolumeCategoryModule;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionDataModule;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionsModule;
import xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlaybackModule;
import xyz.breadloaf.audioplayerroleplay.modules.IUserFacingModule;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleManager;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedSoundModule;

import static xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedPlayback.RANDOM_PLAYBACK_MODULE;
import static xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlayback.STATIC_PLAYBACK_MODULE;

import java.util.UUID;

@Command("roleplay")
public class TestCommands {








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

        for (IUserFacingModule moduleInterface : ModuleManager.ENABLED_MODULES.values()) {
            ModuleKey<?> key = moduleInterface.getModuleKey();
            if (key == null || audioData.getModule(key).isPresent()) {
                context.getSource().sendSuccess(() -> moduleInterface.moduleName().withStyle(ChatFormatting.AQUA).withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(moduleInterface.generalUsageInfo()))), false);
                MutableComponent specificInfo = moduleInterface.itemSpecificInfo(heldItem, audioData);
                if (specificInfo != null) {
                    context.getSource().sendSuccess(() -> specificInfo, false);
                }
            }
        }

    }
}
