package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.CategoryManager;

import java.util.concurrent.CompletableFuture;


public class RegionArgumentType implements ArgumentType<Region> {

    public static final DynamicCommandExceptionType INVALID_REGION = new DynamicCommandExceptionType(o -> Component.literal("Invalid region id: " + o));

    public static RegionArgumentType region() {
        return new RegionArgumentType();
    }

    @Override
    public Region parse(StringReader reader) throws CommandSyntaxException {
        try {
            int argBeginning = reader.getCursor();
            if (!reader.canRead()) {
                reader.skip();
            }

            while (reader.canRead() && (Character.isLetter(reader.peek()) || reader.peek() == '_')) {
                reader.skip();
            }

            String argString = reader.getString().substring(argBeginning, reader.getCursor());

            RegionsModule.LOGGER.info("parsed argument as string, {}", argString);


            return new Region(0, 0, 0, 0, 0, 0);
        } catch (Exception e) {
            RegionsModule.LOGGER.error(e);
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(CategoryManager.CATEGORIES.keySet(), builder);
    }
}
