package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;


public class VolumeCategoryArgumentType implements ArgumentType<CategoryManager.UserVolumeCategory> {

    public static final DynamicCommandExceptionType INVALID_VOLUME_CATEGORY = new DynamicCommandExceptionType(o -> Component.literal("Invalid category id: " + o));

    public static VolumeCategoryArgumentType volumeCategory() {
        return new VolumeCategoryArgumentType();
    }

    @Override
    public CategoryManager.UserVolumeCategory parse(StringReader reader) throws CommandSyntaxException {
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

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(CategoryManager.CATEGORIES.keySet(), builder);
    }
}
