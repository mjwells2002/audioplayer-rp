package xyz.breadloaf.audioplayerroleplay.commands;

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
import xyz.breadloaf.audioplayerroleplay.audioplayer.AudioplayerIntegration;
import xyz.breadloaf.audioplayerroleplay.audioplayer.TestModule;

@Command("roleplay")
public class TestCommands {

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

        audioData.setModule(AudioplayerIntegration.TEST_MODULE, new TestModule());
        audioData.saveToItem(heldItem);
        context.getSource().sendSuccess(() -> Component.literal("Test module applied"), false);
    }

}
