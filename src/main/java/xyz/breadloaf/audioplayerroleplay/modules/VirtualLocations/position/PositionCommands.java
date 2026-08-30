package xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations.position;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

@Command({"roleplay", "location", "named"})
public class PositionCommands {

    @Command("save")
    public void save(CommandContext<CommandSourceStack> context, @Name("id") String id, @Name("location") BlockPos location) throws CommandSyntaxException {
        if (PositionManager.create(id, location)) {
            context.getSource().sendSuccess(() -> Component.literal("Saved location ").append(new Position(id).chatComponent()), false);
            return;
        }
        context.getSource().sendFailure(Component.literal("Failed to save location ID: %s, already exists, update instead?".formatted(id)).withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand("/roleplay location named update %s %d %d %d".formatted(id, location.getX(), location.getZ(), location.getY())))));
    }

    @Command("update")
    public void update(CommandContext<CommandSourceStack> context, @Name("id") Position id, @Name("location") BlockPos location) throws CommandSyntaxException {
        if (PositionManager.update(id.posID, location)) {
            context.getSource().sendSuccess(() -> Component.literal("Saved location ").append(id.chatComponent()), false);
            return;
        }
        context.getSource().sendFailure(Component.literal("Failed to save location ID: %s, doesnt exist, create instead?".formatted(id)).withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand("/roleplay location named save %s %d %d %d".formatted(id, location.getX(), location.getZ(), location.getY())))));
    }

    @Command("info")
    public void info(CommandContext<CommandSourceStack> context, Position pos1) throws CommandSyntaxException {
        context.getSource().sendSuccess(pos1::chatComponent, false);
    }

    @Command("list")
    public void list(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        //TODO: make abstract paginated list command from the randomized feature, reusue it everywhere
        for (String id : PositionManager.getKeys()) {
            context.getSource().sendSuccess(() -> new Position(id).chatComponent(), false);
        }
    }

}
