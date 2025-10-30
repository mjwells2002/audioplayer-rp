package xyz.breadloaf.audioplayerroleplay.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import xyz.breadloaf.audioplayerroleplay.position.Position;
import xyz.breadloaf.audioplayerroleplay.position.PositionManager;

@Command({"roleplay","position"})
public class PositionCommands {

    @Command("save")
    public void save(CommandContext<CommandSourceStack> context, String id, BlockPos location) throws CommandSyntaxException {
        if (PositionManager.create(id,location)) {
            context.getSource().sendSuccess(() -> Component.literal("Saved location ").append(new Position(id).chatComponent()), false);
            return;
        }
        context.getSource().sendFailure(Component.literal("Failed to save position ID: %s, already exists, update instead?".formatted(id)).withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand("/roleplay position update %s %d %d %d".formatted(id,location.getX(),location.getZ(),location.getY())))));

    }

    @Command("update")
    public void update(CommandContext<CommandSourceStack> context, String id, BlockPos location) throws CommandSyntaxException {
        if (PositionManager.update(id,location)) {
            context.getSource().sendSuccess(() -> Component.literal("Saved location ").append(new Position(id).chatComponent()), false);
            return;
        }
        context.getSource().sendFailure(Component.literal("Failed to save position ID: %s, doesnt exists, create instead?".formatted(id)).withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand("/roleplay position save %s %d %d %d".formatted(id,location.getX(),location.getZ(),location.getY())))));

    }

    @Command("info")
    public void info(CommandContext<CommandSourceStack> context, Position pos1) throws CommandSyntaxException {
        context.getSource().sendSuccess(pos1::chatComponent, false);
    }

    @Command("list")
    public void list(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        for (String id : PositionManager.getKeys()) {
            context.getSource().sendSuccess(() -> new Position(id).chatComponent(), false);
        }
    }

}
