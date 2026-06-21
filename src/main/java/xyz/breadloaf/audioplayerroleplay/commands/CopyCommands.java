package xyz.breadloaf.audioplayerroleplay.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.OptionalArgument;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.AudioPlayerConstants;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SkullBlock;
import xyz.breadloaf.audioplayerroleplay.modules.IUserFacingModule;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleManager;

import static xyz.breadloaf.audioplayerroleplay.modules.ModuleUtils.isAudioItem;

@Command("roleplay")
public class CopyCommands {

    @Command("copy")
    public void copy(CommandContext<CommandSourceStack> context, @OptionalArgument boolean force) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack sourceItem = player.getOffhandItem();
        ItemStack targetItem = player.getMainHandItem();
        if (sourceItem.isEmpty()) {
            context.getSource().sendFailure(Component.literal("You are not holding an item in offhand"));
            return;
        }

        AudioData audioData = AudioPlayerApi.instance().getAudioData(sourceItem).orElse(null);

        if (audioData == null) {
            context.getSource().sendFailure(Component.literal("Offhand (source) item has no audio data"));
            return;
        }

        if (!isAudioItem(targetItem)) {
            context.getSource().sendFailure(Component.literal("Mainhand (destination) is not valid audio item"));
            return;
        }
        AudioData audioDataTarget = AudioPlayerApi.instance().getAudioData(targetItem).orElse(null);
        if (audioDataTarget != null && !force) {
            context.getSource().sendFailure(Component.literal("Mainhand (destination) item has audio data, ")
                    .append(Component.literal("[Confirm Overwrite]").withStyle(ChatFormatting.GREEN)
                            .withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand("/roleplay copy true")))));
            return;
        }

        audioData.saveToItem(player.getMainHandItem());

        context.getSource().sendSuccess(() -> Component.literal("Copied audio data from offhand to mainhand"), false);
    }


}
