package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import de.maxhenkel.audioplayer.api.events.AudioEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.modules.IUserFacingModule;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedSoundModule;

public class CustomVolumeCategory implements IUserFacingModule {
    private static String ID = "custom_volume_category";
    public static ModuleKey<VolumeCategoryModule> CUSTOM_VOLUME_CATEGORY_MODULE;

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String register(AudioPlayerApi audioPlayerApi) {
        AudioEvents.POST_PLAY_GOAT_HORN.register(VolumeCategoryHooks::onPostPlay);
        AudioEvents.POST_PLAY_MUSIC_DISC.register(VolumeCategoryHooks::onPostPlay);
        AudioEvents.POST_PLAY_NOTE_BLOCK.register(VolumeCategoryHooks::onPostPlay);

        CUSTOM_VOLUME_CATEGORY_MODULE = audioPlayerApi.registerModuleType(ResourceLocation.fromNamespaceAndPath(AudioPlayerRoleplayMod.MODID, ID), VolumeCategoryModule::new);

        return ID;
    }

    @Override
    public MutableComponent generalUsageInfo() {
        return Component.literal("This replaces the Volume Category with a custom one allowing a separate volume slider for this item");
    }

    @Override
    public @Nullable MutableComponent itemSpecificInfo(ItemStack itemStack, AudioData audioData) {
        return null;
    }

    @Override
    public MutableComponent moduleName() {
        return Component.literal("Custom Volume Category");
    }

    @Override
    public boolean hasModule(AudioData audioData) {
        return audioData.getModule(CUSTOM_VOLUME_CATEGORY_MODULE).isPresent();
    }
}
