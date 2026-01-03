package xyz.breadloaf.audioplayerroleplay.position;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
import de.maxhenkel.audioplayer.AudioPlayerMod;
import de.maxhenkel.audioplayer.audioloader.AudioStorageManager;
import de.maxhenkel.audioplayer.command.ServerFileArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PositionArgument {
    public static final DynamicCommandExceptionType NO_RELATIVE = new DynamicCommandExceptionType(o -> Component.literal("Relative coordinates not allowed"));

    public static class PositionArgumentSupplier implements ArgumentTypeSupplier<CommandSourceStack, Position, String> {
        public ArgumentType<String> get() {
            return StringArgumentType.string();
        }

        public SuggestionProvider<CommandSourceStack> getSuggestionProvider() {
            return new ServerFileArgument.ServerFileSuggestionProvider();
        }
    }

    public static class PositionArgumentTypeConverter implements ArgumentTypeConverter<CommandSourceStack, String, Position> {
        public @Nullable Position convert(CommandContext<CommandSourceStack> commandContext, String s) throws CommandSyntaxException {
            StringReader reader = new StringReader(s);
            int argBeginning = reader.getCursor();
            if (!reader.canRead()) {
                reader.skip();
            }

            if (reader.canRead() && (Character.isDigit(reader.peek()) || reader.peek() == '~' || reader.peek() == '-')) {
                WorldCoordinates coordinates = WorldCoordinates.parseInt(reader);

                if (coordinates.isXRelative() || coordinates.isYRelative() || coordinates.isZRelative()) {
                    throw NO_RELATIVE.createWithContext(reader, null);
                }


                return new Position((int) coordinates.x().get(0), (int) coordinates.y().get(0), (int) coordinates.z().get(0));
            } else {
                while (reader.canRead() && (Character.isLetterOrDigit(reader.peek()) || reader.peek() == '_')) {
                    reader.skip();
                }

                String argString = reader.getString().substring(argBeginning, reader.getCursor());
                //TODO: validate exists
                return new Position(argString);
            }
        }
    }

    public static final class PositionArgumentSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            String string = builder.getRemaining();
            Collection<SharedSuggestionProvider.TextCoordinates> collection;
            if (!string.isEmpty() && string.charAt(0) == '^') {
                collection = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
            } else {
                collection = ((SharedSuggestionProvider) context.getSource()).getRelevantCoordinates();
            }
            Predicate<String> predicate = Commands.createValidator(WorldCoordinates::parseInt);

            List<String> list = Lists.newArrayList();
            if (Strings.isNullOrEmpty(string)) {
                for (SharedSuggestionProvider.TextCoordinates textCoordinates : collection) {
                    String string2 = textCoordinates.x + " " + textCoordinates.y + " " + textCoordinates.z;
                    if (predicate.test(string2)) {
                        list.add(textCoordinates.x);
                        list.add(textCoordinates.x + " " + textCoordinates.y);
                        list.add(string2);
                    }
                }
            } else {
                String[] strings = string.split(" ");
                if (strings.length == 1) {
                    for (SharedSuggestionProvider.TextCoordinates textCoordinates2 : collection) {
                        String string3 = strings[0] + " " + textCoordinates2.y + " " + textCoordinates2.z;
                        if (predicate.test(string3)) {
                            list.add(strings[0] + " " + textCoordinates2.y);
                            list.add(string3);
                        }
                    }
                } else if (strings.length == 2) {
                    for (SharedSuggestionProvider.TextCoordinates textCoordinates2 : collection) {
                        String string3 = strings[0] + " " + strings[1] + " " + textCoordinates2.z;
                        if (predicate.test(string3)) {
                            list.add(string3);
                        }
                    }
                }
            }

            list.addAll(PositionManager.getKeys());

            return SharedSuggestionProvider.suggest(list, builder);
        }
    }
}
