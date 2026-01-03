package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;


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
import java.util.concurrent.CompletableFuture;


public class VolumeCategory {
    public static final DynamicCommandExceptionType INVALID_VOLUME_CATEGORY = new DynamicCommandExceptionType(o -> Component.literal("Invalid category id: " + o));

    public static class Supplier implements ArgumentTypeSupplier<CommandSourceStack, CategoryManager.UserVolumeCategory, String> {
        public ArgumentType<String> get() {
            return StringArgumentType.string();
        }

        public SuggestionProvider<CommandSourceStack> getSuggestionProvider() {
            return new VolumeCategory.SuggestionsProvider();
        }
    }

    public static class TypeConverter implements ArgumentTypeConverter<CommandSourceStack, String, CategoryManager.UserVolumeCategory> {
        public CategoryManager.UserVolumeCategory convert(CommandContext<CommandSourceStack> commandContext, String s) throws CommandSyntaxException {
            StringReader reader = new StringReader(s);
            int argBeginning = reader.getCursor();
            if (!reader.canRead()) {
                reader.skip();
            }

            while (reader.canRead() && (Character.isLetterOrDigit(reader.peek()) || reader.peek() == '_')) {
                reader.skip();
            }

            String argString = reader.getString().substring(argBeginning, reader.getCursor());

            CategoryManager.UserVolumeCategory category = CategoryManager.CATEGORIES.get(argString);
            if (category == null) {
                throw INVALID_VOLUME_CATEGORY.createWithContext(reader, argString);
            }

            return category;
        }
    }

    public static final class SuggestionsProvider implements SuggestionProvider<CommandSourceStack> {
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            return SharedSuggestionProvider.suggest(CategoryManager.CATEGORIES.keySet(), builder);
        }
    }
}
