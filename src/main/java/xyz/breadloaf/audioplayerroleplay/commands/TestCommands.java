package xyz.breadloaf.audioplayerroleplay.commands;

import com.mojang.brigadier.context.CommandContext;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

@Command("roleplay")
public class TestCommands {

    @RequiresPermission("audioplayer_roleplay.test")
    @Command("hello")
    public void hello(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Hello!"), false);
    }

}
