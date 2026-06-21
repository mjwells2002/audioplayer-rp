package xyz.breadloaf.audioplayerroleplay.modules;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.maxhenkel.admiral.argumenttype.ArgumentTypeConverter;
import de.maxhenkel.admiral.argumenttype.ArgumentTypeSupplier;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioFileMetadata;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.Region;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class AudioFileArgument {
    public static final DynamicCommandExceptionType INVALID_ID = new DynamicCommandExceptionType(o -> Component.literal("Invalid audio id: " + o));

    public static class Supplier implements ArgumentTypeSupplier<CommandSourceStack, AudioFile, String> {
        public ArgumentType<String> get() {
            return StringArgumentType.string();
        }

        public SuggestionProvider<CommandSourceStack> getSuggestionProvider() {
            return new AudioFileArgument.SuggestionsProvider();
        }
    }

    public static class TypeConverter implements ArgumentTypeConverter<CommandSourceStack, String, AudioFile> {
        public AudioFile convert(CommandContext<CommandSourceStack> commandContext, String argString) throws CommandSyntaxException {
            StringReader stringReader = new StringReader(argString);

            try {
                UUID uuid = UUID.fromString(argString);
                return new AudioFile(uuid);
            } catch (Exception ignored) {

            }

            for (AudioFileMetadata metadata : AudioPlayerApi.instance().getAllMetadata()) {
                if (argString.equals(metadata.getFileName())) {
                    return new AudioFile(metadata.getAudioId());
                }
            }

            throw INVALID_ID.createWithContext(stringReader, argString);
        }
    }

    public static final class SuggestionsProvider implements SuggestionProvider<CommandSourceStack> {
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            if (builder.getRemaining().isEmpty()) {
                return SharedSuggestionProvider.suggest(List.of(""), builder);
            }

            String input = builder.getRemaining();

            if (input.startsWith("\"")) {
                if (input.endsWith("\"")) {
                    input = input.substring(1,input.length()-1);
                } else {
                    input = input.substring(1);
                }
            }


            String finalInput = input;
            List<String> sortedSuggestions = AudioPlayerApi.instance().getAllMetadata()
                    .stream()
                    .map(x -> Objects.requireNonNullElse(x.getFileName(), x.getAudioId().toString()))
                    .filter(x -> x.toLowerCase().contains(finalInput.toLowerCase()))
                    .sorted(Comparator.comparingInt(p -> levenshtein(p,finalInput.toLowerCase())))
                    .map(x -> x.contains(" ") ? "\"%s\"".formatted(x) : x)
                    .peek(builder::suggest)
                    .toList();

            Suggestions suggestions = builder.build();
            return CompletableFuture.completedFuture(
                    new Suggestions(
                            suggestions.getRange(),
                            sortedSuggestions.stream()
                                    .map(x -> new Suggestion(suggestions.getRange(),x))
                                    .toList()
                    )
            );
        }
    }

    private static int levenshtein(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
}
