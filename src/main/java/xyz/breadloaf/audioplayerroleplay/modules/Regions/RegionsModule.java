package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import de.maxhenkel.admiral.argumenttype.ArgumentTypeRegistry;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import de.maxhenkel.audioplayer.api.events.AudioEvents;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.modules.BaseModuleCommand;
import xyz.breadloaf.audioplayerroleplay.modules.IUserFacingModule;

import java.util.Collection;
import java.util.List;

import static xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod.*;

public class RegionsModule implements IUserFacingModule {

    static String ID = "regions";
    public static ModuleKey<RegionDataModule> REGIONS_DATA_MODULE;
    public static RegionsConfig REGIONS_CONFIG = ConfigBuilder.builder(RegionsConfig::new).path(getModuleConfigFolder(ID).resolve("regions.properties")).build();

    @Override
    public String getID() {
        return ID;
    }

    static Logger LOGGER = getModuleLogger(ID);

    @Override
    public String register(AudioPlayerApi audioPlayerApi) {
        AudioEvents.POST_PLAY_GOAT_HORN.register(RegionHooks::onPostPlay);
        AudioEvents.POST_PLAY_NOTE_BLOCK.register(RegionHooks::onPostPlay);
        AudioEvents.POST_PLAY_MUSIC_DISC.register(RegionHooks::onPostPlay);

        AudioEvents.GET_DISTANCE.register(RegionHooks::onGetDistance);
        REGIONS_DATA_MODULE = audioPlayerApi.registerModuleType(ResourceLocation.fromNamespaceAndPath(AudioPlayerRoleplayMod.MODID, ID), RegionDataModule::new);

        return ID;
    }

    @Override
    public MutableComponent generalUsageInfo() {
        return Component.literal("Modifies the item to play only within a set cube region, overriding the range option");
    }

    @Override
    public @Nullable MutableComponent itemSpecificInfo(ItemStack itemStack, AudioData audioData) {
        RegionDataModule regionDataModule = audioData.getModule(REGIONS_DATA_MODULE).orElse(null);
        if (regionDataModule != null) {
            Region region = regionDataModule.region;
            return region.chatComponent();
        }
        return null;
    }

    @Override
    public MutableComponent moduleName() {
        return Component.literal("Cube Regions");
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
        return RegionCommands.class;
    }

    @Override
    public @Nullable Collection<Class<?>> getAdditionalCommandClasses() {
        return List.of(NamedRegionCommands.class);
    }

    @Override
    public void serverStartingHook() {
        RegionManager.load();
    }

    @Override
    public void serverStoppingHook() {
        RegionManager.save();
    }

    @Override
    public void earlyRegistrationHook() {
        net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MODID, ID), RegionArgumentType.class, SingletonArgumentInfo.contextFree(RegionArgumentType::region));
    }

    @Override
    public void registerArgumentTypes(ArgumentTypeRegistry argumentTypeRegistry) {
        argumentTypeRegistry.register(Region.class, RegionArgumentType::region);
    }

    @Override
    public @Nullable ModuleKey<?> getModuleKey() {
        return REGIONS_DATA_MODULE;
    }
}
