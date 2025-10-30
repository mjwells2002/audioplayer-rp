 package xyz.breadloaf.audioplayerroleplay.position;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;


 public class PositionArgumentType implements ArgumentType<Position> {
    public static final DynamicCommandExceptionType NO_RELATIVE = new DynamicCommandExceptionType(o -> Component.literal("Relative coordinates not allowed"));

    public static PositionArgumentType region() {
        return new PositionArgumentType();
    }

     @Override
     public Position parse(StringReader reader) throws CommandSyntaxException {
         return parse(reader,null);
     }

    @Override
    public <S> Position parse(StringReader reader, S source) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }

        if (reader.canRead() && (Character.isDigit(reader.peek()) || reader.peek() == '~' || reader.peek() == '-')) {
            WorldCoordinates coordinates = WorldCoordinates.parseInt(reader);

            if (coordinates.isXRelative() || coordinates.isYRelative() || coordinates.isZRelative()) {
                throw NO_RELATIVE.createWithContext(reader,null);
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

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof SharedSuggestionProvider)) {
            return Suggestions.empty();
        } else {
            String string = builder.getRemaining();
            Collection<SharedSuggestionProvider.TextCoordinates> collection;
            if (!string.isEmpty() && string.charAt(0) == '^') {
                collection = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
            } else {
                collection = ((SharedSuggestionProvider)context.getSource()).getRelevantCoordinates();
            }
            Predicate<String> predicate = Commands.createValidator(WorldCoordinates::parseInt);

            List<String> list = Lists.newArrayList();
            if (Strings.isNullOrEmpty(string)) {
                for(SharedSuggestionProvider.TextCoordinates textCoordinates : collection) {
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
                    for(SharedSuggestionProvider.TextCoordinates textCoordinates2 : collection) {
                        String string3 = strings[0] + " " + textCoordinates2.y + " " + textCoordinates2.z;
                        if (predicate.test(string3)) {
                            list.add(strings[0] + " " + textCoordinates2.y);
                            list.add(string3);
                        }
                    }
                } else if (strings.length == 2) {
                    for(SharedSuggestionProvider.TextCoordinates textCoordinates2 : collection) {
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
