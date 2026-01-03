package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xyz.breadloaf.audioplayerroleplay.modules.BaseModuleCommand;

@Command({"roleplay", "regions"})
public class RegionCommands extends BaseModuleCommand {

    @Command("apply")
    public void applyAnonymous(CommandContext<CommandSourceStack> context, @Name("pos 1") BlockPos p1, @Name("pos 2") BlockPos p2) throws CommandSyntaxException {
        doApply(context, new Region(p1, p2));
    }

    @Command("apply")
    public void applyNamed(CommandContext<CommandSourceStack> context, @Name("region") Region region) throws CommandSyntaxException {
        doApply(context, region);
    }

    private static void doApply(CommandContext<CommandSourceStack> context, Region region) throws CommandSyntaxException {
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

        audioData.setModule(RegionsModule.REGIONS_DATA_MODULE, new RegionDataModule(region));
        audioData.saveToItem(heldItem);

        context.getSource().sendSuccess(() -> Component.literal("Applied region to item").withStyle(ChatFormatting.AQUA), false);
    }

    @Override
    public String getModuleKey() {
        return RegionsModule.ID;
    }


}
