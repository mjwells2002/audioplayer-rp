package xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.AudioDataModule;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import de.maxhenkel.audioplayer.api.events.AudioEvents;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.modules.BaseModuleCommand;
import xyz.breadloaf.audioplayerroleplay.modules.IUserFacingModule;
import de.maxhenkel.admiral.argumenttype.ArgumentTypeRegistry;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionHooks;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionsConfig;
import xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations.position.Position;
import xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations.position.PositionArgument;
import xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations.position.PositionCommands;

import java.util.Collection;
import java.util.List;

import static xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod.getModuleConfigFolder;

public class VirtualLocations implements IUserFacingModule {
    public static ModuleKey<VirtualLocationModule> VIRTUAL_LOCATION_PLAYBACK_MODULE;
    static String ID = "vlocation";
    public static VirtualLocationConfig VLOCATION_CONFIG = ConfigBuilder.builder(VirtualLocationConfig::new).path(getModuleConfigFolder(ID).resolve("vlocation.properties")).build();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String register(AudioPlayerApi audioPlayerApi) {
        VIRTUAL_LOCATION_PLAYBACK_MODULE = audioPlayerApi.registerModuleType(Identifier.fromNamespaceAndPath(AudioPlayerRoleplayMod.MODID, ID), VirtualLocationModule::new);

        AudioEvents.PLAY_NOTE_BLOCK.register(LocationHooks::onPlay);
        AudioEvents.PLAY_GOAT_HORN.register(LocationHooks::onPlay);
        AudioEvents.PLAY_MUSIC_DISC.register(LocationHooks::onPlay);

        return ID;
    }

    @Override
    public MutableComponent generalUsageInfo() {
        return Component.literal("Moves the location of playback of the item to a new location");
    }

    @Override
    @Nullable
    public MutableComponent itemSpecificInfo(ItemStack itemStack, AudioData audioData) {
        VirtualLocationModule virtualLocationModule = audioData.getModule(VIRTUAL_LOCATION_PLAYBACK_MODULE).orElse(null);
        if (virtualLocationModule != null) {
            return virtualLocationModule.position.chatComponent();
        }
        return null;
    }

    @Override
    public MutableComponent moduleName() {
        return Component.literal("Virtual Location Override");
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public boolean canBeDisabled() {
        return true;
    }

    @Override
    public Class<? extends BaseModuleCommand> getCommandClass() {
        return VirtualLocationCommands.class;
    }

    @Override
    public @Nullable Collection<Class<?>> getAdditionalCommandClasses() {
        return List.of(PositionCommands.class);
    }

    @Override
    public void serverStartingHook() {

    }

    @Override
    public void serverStoppingHook() {

    }

    @Override
    public void earlyRegistrationHook() {

    }

    @Override
    public void registerArgumentTypes(ArgumentTypeRegistry argumentTypeRegistry) {
        argumentTypeRegistry.register(Position.class, new PositionArgument.PositionArgumentSupplier(), new PositionArgument.PositionArgumentTypeConverter());
    }

    @Override
    public @Nullable ModuleKey<?> getModuleKey() {
        return VIRTUAL_LOCATION_PLAYBACK_MODULE;
    }

    @Override
    public @Nullable AudioDataModule getBareDataModule() {
        return new VirtualLocationModule();
    }
}
