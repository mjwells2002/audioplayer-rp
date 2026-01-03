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
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.CategoryManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class RegionArugment {
    public static final DynamicCommandExceptionType INVALID_REGION = new DynamicCommandExceptionType(o -> Component.literal("Invalid region id: " + o));

    public static class Supplier implements ArgumentTypeSupplier<CommandSourceStack, Region, String> {
        public ArgumentType<String> get() {
            return StringArgumentType.string();
        }

        public SuggestionProvider<CommandSourceStack> getSuggestionProvider() {
            return new RegionArugment.SuggestionsProvider();
        }
    }

    public static class TypeConverter implements ArgumentTypeConverter<CommandSourceStack, String, Region> {
        public Region convert(CommandContext<CommandSourceStack> commandContext, String s) throws CommandSyntaxException {
            StringReader reader = new StringReader(s);
            int argBeginning = reader.getCursor();
            if (!reader.canRead()) {
                reader.skip();
            }

            while (reader.canRead() && (Character.isLetter(reader.peek()) || reader.peek() == '_')) {
                reader.skip();
            }

            String argString = reader.getString().substring(argBeginning, reader.getCursor());

            if (RegionManager.REGIONS != null) {
                boolean regionExists = RegionManager.REGIONS.id_to_minmax.containsKey(argString);
                if (!regionExists) {
                    throw INVALID_REGION.createWithContext(reader, argString);
                }
            }

            return new Region(argString);
        }
    }

    public static final class SuggestionsProvider implements SuggestionProvider<CommandSourceStack> {
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            if (RegionManager.REGIONS != null) {
                SharedSuggestionProvider.suggest(RegionManager.REGIONS.id_to_minmax.keySet(), builder);
            }
            return SharedSuggestionProvider.suggest(List.of(""), builder);
        }
    }
}
