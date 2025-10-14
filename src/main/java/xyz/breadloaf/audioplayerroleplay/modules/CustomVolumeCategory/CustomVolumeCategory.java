package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import de.maxhenkel.admiral.argumenttype.ArgumentTypeRegistry;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import de.maxhenkel.audioplayer.api.events.AudioEvents;
import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.ConfigBuilderImpl;
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
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedSoundModule;

import static xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod.MODID;
import static xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod.getModuleLogger;

public class CustomVolumeCategory implements IUserFacingModule {
    static String ID = "custom_volume_category";
    public static ModuleKey<VolumeCategoryModule> CUSTOM_VOLUME_CATEGORY_MODULE;
    public static VolumeConfig VOLUME_CATEGORIES = new VolumeConfig(AudioPlayerRoleplayMod.getModuleConfigFolder(ID).resolve("categories.properties"));
    static Logger LOGGER = getModuleLogger(ID);
    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String register(AudioPlayerApi audioPlayerApi) {
        AudioEvents.POST_PLAY_GOAT_HORN.register(VolumeCategoryHooks::onPostPlay);
        AudioEvents.POST_PLAY_MUSIC_DISC.register(VolumeCategoryHooks::onPostPlay);
        AudioEvents.POST_PLAY_NOTE_BLOCK.register(VolumeCategoryHooks::onPostPlay);

        CUSTOM_VOLUME_CATEGORY_MODULE = audioPlayerApi.registerModuleType(ResourceLocation.fromNamespaceAndPath(MODID, ID), VolumeCategoryModule::new);

        CategoryManager.reloadCategories();
        return ID;
    }

    @Override
    public MutableComponent generalUsageInfo() {
        return Component.literal("This replaces the Volume Category with a custom one allowing a separate volume slider for this item");
    }

    @Override
    public @Nullable MutableComponent itemSpecificInfo(ItemStack itemStack, AudioData audioData) {
        VolumeCategoryModule data = audioData.getModule(CUSTOM_VOLUME_CATEGORY_MODULE).orElse(null);
        if (data != null) {
            return Component.literal("Volume Category: ").append(CategoryManager.getChatComponentFor(data.id));
        }
        return Component.empty();
    }

    @Override
    public MutableComponent moduleName() {
        return Component.literal("Custom Volume Category");
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
        return VolumeCategoryCommands.class;
    }

    @Override
    public void earlyRegistrationHook() {
        net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MODID,ID),VolumeCategoryArgumentType.class, SingletonArgumentInfo.contextFree(VolumeCategoryArgumentType::volumeCategory));
    }

    @Override
    public void registerArgumentTypes(ArgumentTypeRegistry argumentTypeRegistry) {
        argumentTypeRegistry.register(CategoryManager.UserVolumeCategory.class, VolumeCategoryArgumentType::volumeCategory);
    }

    @Override
    public @Nullable ModuleKey<?> getModuleKey() {
        return CUSTOM_VOLUME_CATEGORY_MODULE;
    }
}
