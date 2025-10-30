package xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xyz.breadloaf.audioplayerroleplay.modules.BaseModuleCommand;

import static xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlayback.STATIC_PLAYBACK_MODULE;

@Command({"roleplay", "static"})
public class StaticCommands extends BaseModuleCommand {

    @Command("apply")
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

        audioData.setModule(STATIC_PLAYBACK_MODULE, new StaticPlaybackModule());
        audioData.saveToItem(heldItem);
        context.getSource().sendSuccess(() -> Component.literal("Static module applied"), false);
    }

    @Override
    public String getModuleKey() {
        return StaticPlayback.ID;
    }
}
