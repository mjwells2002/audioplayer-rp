package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.argument;


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
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.PlaylistFile;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.PlaylistManager;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class PlaylistArg {
    public static final DynamicCommandExceptionType INVALID_PLAYLIST = new DynamicCommandExceptionType(o -> Component.literal("Invalid playlist id: " + o));

    public static class Supplier implements ArgumentTypeSupplier<CommandSourceStack, PlaylistFile.Playlist, String> {
        public ArgumentType<String> get() {
            return StringArgumentType.string();
        }

        public SuggestionProvider<CommandSourceStack> getSuggestionProvider() {
            return new SuggestionsProvider();
        }
    }

    public static class TypeConverter implements ArgumentTypeConverter<CommandSourceStack, String, PlaylistFile.Playlist> {
        public PlaylistFile.Playlist convert(CommandContext<CommandSourceStack> commandContext, String argString) throws CommandSyntaxException {
            StringReader reader = new StringReader(argString);

            if (PlaylistManager.PLAYLISTS != null) {
                boolean regionExists = PlaylistManager.PLAYLISTS.playlists.containsKey(argString);
                if (!regionExists) {
                    throw INVALID_PLAYLIST.createWithContext(reader, argString);
                }
            }

            return PlaylistManager.getPlaylist(argString);
        }
    }

    public static final class SuggestionsProvider implements SuggestionProvider<CommandSourceStack> {
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            if (PlaylistManager.PLAYLISTS != null) {
                SharedSuggestionProvider.suggest(PlaylistManager.PLAYLISTS.playlists.keySet(), builder);
            }
            return SharedSuggestionProvider.suggest(List.of(""), builder);
        }
    }
}
