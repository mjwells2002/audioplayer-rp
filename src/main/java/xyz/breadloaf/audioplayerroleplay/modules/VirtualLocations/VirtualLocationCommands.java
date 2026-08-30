package xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations;

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
import xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionDataModule;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionsModule;
import xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations.position.Position;

@Command({"roleplay", "location"})
public class VirtualLocationCommands extends BaseModuleCommand {

    @Command("apply")
    public void setVirtualLocation(CommandContext<CommandSourceStack> context, @Name("position") BlockPos position) throws CommandSyntaxException {
        setVirtualLocation(context, new Position(position.getX(),position.getY(),position.getZ()));
    }

    @Command("apply")
    public void setVirtualLocation(CommandContext<CommandSourceStack> context, @Name("id") Position position) throws CommandSyntaxException {
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

        audioData.setModule(VirtualLocations.VIRTUAL_LOCATION_PLAYBACK_MODULE, new VirtualLocationModule(position));
        audioData.saveToItem(heldItem);

        context.getSource().sendSuccess(() -> Component.literal("Applied location to item").withStyle(ChatFormatting.AQUA), false);
    }

    @Override
    public String getModuleKey() {
        return VirtualLocations.ID;
    }
}
