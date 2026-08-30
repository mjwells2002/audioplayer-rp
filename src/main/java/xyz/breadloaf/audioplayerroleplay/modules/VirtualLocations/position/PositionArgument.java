package xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations.position;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.maxhenkel.admiral.argumenttype.ArgumentTypeConverter;
import de.maxhenkel.admiral.argumenttype.ArgumentTypeSupplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PositionArgument {
    public static class PositionArgumentSupplier implements ArgumentTypeSupplier<CommandSourceStack, Position, String> {
        public ArgumentType<String> get() {
            return StringArgumentType.string();
        }

        public SuggestionProvider<CommandSourceStack> getSuggestionProvider() {
            return new PositionArgumentSuggestionProvider();
        }
    }

    public static class PositionArgumentTypeConverter implements ArgumentTypeConverter<CommandSourceStack, String, Position> {
        public @Nullable Position convert(CommandContext<CommandSourceStack> commandContext, String s) throws CommandSyntaxException {
            StringReader reader = new StringReader(s);

            String argString = reader.getString();
            return new Position(argString);
        }
    }

    public static final class PositionArgumentSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            List<String> list = Lists.newArrayList();

            list.addAll(PositionManager.getKeys());

            return SharedSuggestionProvider.suggest(list, builder);
        }
    }
}
