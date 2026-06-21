package xyz.breadloaf.audioplayerroleplay.modules.Regions;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.maxhenkel.admiral.argumenttype.ArgumentTypeConverter;
import de.maxhenkel.admiral.argumenttype.ArgumentTypeSupplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class RegionModeArugment {
    public static final DynamicCommandExceptionType INVALID_REGION_MODE = new DynamicCommandExceptionType(o -> Component.literal("Invalid region mode: " + o));

    public static class Supplier implements ArgumentTypeSupplier<CommandSourceStack, RegionMode, String> {
        public ArgumentType<String> get() {
            return StringArgumentType.string();
        }

        public SuggestionProvider<CommandSourceStack> getSuggestionProvider() {
            return new RegionModeArugment.SuggestionsProvider();
        }
    }

    public static class TypeConverter implements ArgumentTypeConverter<CommandSourceStack, String, RegionMode> {
        public RegionMode convert(CommandContext<CommandSourceStack> commandContext, String s) throws CommandSyntaxException {
            StringReader reader = new StringReader(s);
            int argBeginning = reader.getCursor();
            if (!reader.canRead()) {
                reader.skip();
            }

            while (reader.canRead() && (Character.isLetter(reader.peek()) || reader.peek() == '_')) {
                reader.skip();
            }

            String argString = reader.getString().substring(argBeginning, reader.getCursor());


            try {
                return RegionMode.valueOf(argString);
            } catch (IllegalArgumentException ignored) {
                throw INVALID_REGION_MODE.createWithContext(reader, argString);
            }
        }
    }

    public static final class SuggestionsProvider implements SuggestionProvider<CommandSourceStack> {
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            return SharedSuggestionProvider.suggest(Arrays.stream(RegionMode.values()).map(RegionMode::toString), builder);
        }
    }
}
