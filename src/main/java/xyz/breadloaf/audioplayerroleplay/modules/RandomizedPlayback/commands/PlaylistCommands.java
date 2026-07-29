package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import de.maxhenkel.admiral.annotations.OptionalArgument;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioFileMetadata;
import de.maxhenkel.audioplayer.api.importer.AudioImportInfo;
import de.maxhenkel.audioplayer.api.importer.ImportedAudio;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.tuple.Pair;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.importer.BulkImporterProvider;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleUtils;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.DummyAudioFileMetadata;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.PlaylistFile;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.PlaylistManager;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.IntStream;

@Command({"roleplay", "randomized", "playlist"})
public class PlaylistCommands {
    @Command("list")
    public void list(CommandContext<CommandSourceStack> context, @Name("playlist_id") PlaylistFile.Playlist playlist, @OptionalArgument @Name("page") int page) {
        MutableComponent mutableComponent = Component.empty();
        AudioPlayerApi audioPlayerApi = AudioPlayerApi.instance();

        mutableComponent.append(Component.literal("Sounds in playlist: " + playlist.id.toString()).withStyle(ChatFormatting.AQUA));
        mutableComponent.append("\n");
        int pageSize = 10;
        int pageStartIndex = page * pageSize;
        int pageEndIndex = pageStartIndex + pageSize;
        int totalPages = (int) Math.ceil(playlist.soundIds.size() / (double) pageSize) - 1;

        boolean hasNextPage = true;

        if (pageEndIndex >= playlist.soundIds.size()) {
            pageEndIndex = playlist.soundIds.size()-1;
            hasNextPage = false;
        }

        if (pageStartIndex >= playlist.soundIds.size()) {
            return;
        }

        IntStream.range(pageStartIndex,pageEndIndex)
                .mapToObj(playlist.soundIds::get)
                .map(x -> audioPlayerApi.getAudioFileMetadata(x).orElse(new DummyAudioFileMetadata(x, "Missing File")))
                .map(ModuleUtils::getAudioMetadataComponent)
                .peek(mutableComponent::append)
                .forEach(_ -> mutableComponent.append("\n"));

        if (page > 0) {
            mutableComponent.append(Component.literal("[PREV]")
                    .withStyle(ChatFormatting.GREEN)
                    .withStyle(x -> x .withClickEvent(new ClickEvent.SuggestCommand("/roleplay randomized playlist list %s %d".formatted(playlist.id, page - 1)))));
        } else {
            mutableComponent.append(Component.literal("      "));
        }
        mutableComponent.append(Component.literal("    %d/%d    ".formatted(page,totalPages)));
        if (hasNextPage) {
            mutableComponent.append(Component.literal("[NEXT]")
                    .withStyle(ChatFormatting.GREEN)
                    .withStyle(x -> x .withClickEvent(new ClickEvent.SuggestCommand("/roleplay randomized playlist list %s %d".formatted(playlist.id, page + 1)))));
        }  else {
            mutableComponent.append(Component.literal("      "));
        }

        context.getSource().sendSuccess(() -> mutableComponent, false);
    }

    @Command("append")
    public void append(CommandContext<CommandSourceStack> context, @Name("playlist_id") PlaylistFile.Playlist playlist, @Name("sound") AudioFileMetadata toAdd) {
        playlist.append(toAdd.getAudioId());
        PlaylistManager.save();
        context.getSource().sendSuccess(() -> Component.literal("Added sound to playlist"), false);
    }

    @Command("remove")
    public void remove(CommandContext<CommandSourceStack> context, @Name("playlist_id") PlaylistFile.Playlist playlist, @Name("sound") AudioFileMetadata toRemove) {
        playlist.remove(toRemove.getAudioId());
        PlaylistManager.save();
        context.getSource().sendSuccess(() -> Component.literal("Removed sound from playlist"), false);
    }

    @Command("create")
    public void create(CommandContext<CommandSourceStack> context, @Name("playlist_id") String pid) {
        PlaylistManager.getPlaylist(pid);
        PlaylistManager.save();
        context.getSource().sendSuccess(() -> Component.literal("Created playlist %s".formatted(pid)), false);
    }

    @Command("import")
    public void folderImport(CommandContext<CommandSourceStack> context, @Name("folder") String folder, @Name("playlist_id") PlaylistFile.Playlist playlist) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Path importFolder = AudioPlayerApi.instance().getUploadFolder().resolve(folder);
        ArrayList<Pair<CompletableFuture<ImportedAudio>, String>> futures = new ArrayList<>();
        File[] files = importFolder.toFile().listFiles();
        if (files == null) {
            context.getSource().sendFailure(Component.literal("No audio files found to import"));
            return;
        }
        for (File file : files) {
            futures.add(Pair.of(AudioPlayerApi.instance().importAudio(new BulkImporterProvider(file), player, false),file.getName()));
        }
        AudioPlayerRoleplayMod.WORKER.execute(() -> {
            int failedImport = 0;
            int impoprted = 0;
            for (Pair<CompletableFuture<ImportedAudio>, String> futurePair : futures) {
                player.sendOverlayMessage(Component.literal("Importing %d/%d files imported".formatted(failedImport+impoprted,futures.size())));
                try {
                    ImportedAudio x = futurePair.getLeft().join();
                    playlist.append(x.getAudioId());
                    impoprted++;
                } catch (CompletionException e) {
                    player.sendSystemMessage(
                            Component.literal("Failed to import: ")
                                    .withStyle(ChatFormatting.RED)
                                    .append(Component.literal(futurePair.getRight()).withStyle(ChatFormatting.GRAY))
                                    .append(Component.literal("\n"))
                                    .append(Component.literal(e.getCause().getMessage()))
                    );
                    failedImport++;
                }
            }
            player.sendSystemMessage(Component.literal("Playlist import completed ")
                    .append(" %d ".formatted(impoprted)).append("Files imported")
                    .append(" %d ".formatted(failedImport)).append("Files failed to import"));

            PlaylistManager.save();
        });
    }
}
