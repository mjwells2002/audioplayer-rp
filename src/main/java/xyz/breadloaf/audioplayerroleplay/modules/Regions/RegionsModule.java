package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import de.maxhenkel.admiral.argumenttype.ArgumentTypeRegistry;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import de.maxhenkel.audioplayer.api.events.AudioEvents;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
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
        AudioEvents.PLAY_NOTE_BLOCK.register(RegionHooks::onPlay);
        AudioEvents.POST_PLAY_GOAT_HORN.register(RegionHooks::onPostPlay);
        AudioEvents.POST_PLAY_NOTE_BLOCK.register(RegionHooks::onPostPlay);
        AudioEvents.POST_PLAY_MUSIC_DISC.register(RegionHooks::onPostPlay);

        AudioEvents.GET_DISTANCE.register(RegionHooks::onGetDistance);
        REGIONS_DATA_MODULE = audioPlayerApi.registerModuleType(Identifier.fromNamespaceAndPath(AudioPlayerRoleplayMod.MODID, ID), RegionDataModule::new);

        return ID;
    }

    @Override
    public MutableComponent generalUsageInfo() {
        return Component.literal("Modifies the item to play only within a set cube region, in CLIP mode this overrides the range option ")
                .append(Component.literal("in FALLOFF mode this applies falloff within the range outside of the region"));
    }

    @Override
    public @Nullable MutableComponent itemSpecificInfo(ItemStack itemStack, AudioData audioData) {
        RegionDataModule regionDataModule = audioData.getModule(REGIONS_DATA_MODULE).orElse(null);
        if (regionDataModule != null) {
            Region region = regionDataModule.region;
            MutableComponent mutableComponent = Component.empty();
            mutableComponent.append(Component.literal("Mode: "));
            mutableComponent.append(Component.literal(regionDataModule.regionMode.toString()).withStyle(ChatFormatting.AQUA));
            mutableComponent.append(Component.literal("\n"));
            mutableComponent.append(region.chatComponent());
            return mutableComponent;
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

    }

    @Override
    public void registerArgumentTypes(ArgumentTypeRegistry argumentTypeRegistry) {
        argumentTypeRegistry.register(Region.class, new RegionArugment.Supplier(), new RegionArugment.TypeConverter());
        argumentTypeRegistry.register(RegionMode.class, new RegionModeArugment.Supplier(), new RegionModeArugment.TypeConverter());
    }

    @Override
    public @Nullable ModuleKey<?> getModuleKey() {
        return REGIONS_DATA_MODULE;
    }
}
