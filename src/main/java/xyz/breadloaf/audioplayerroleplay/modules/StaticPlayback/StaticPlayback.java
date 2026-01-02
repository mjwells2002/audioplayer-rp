package xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback;

import de.maxhenkel.admiral.argumenttype.ArgumentTypeRegistry;
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
import xyz.breadloaf.audioplayerroleplay.modules.BaseModuleCommand;
import xyz.breadloaf.audioplayerroleplay.modules.IUserFacingModule;

import java.util.Collection;

public class StaticPlayback implements IUserFacingModule {

    public static ModuleKey<StaticPlaybackModule> STATIC_PLAYBACK_MODULE;
    static String ID = "static";

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String register(AudioPlayerApi audioPlayerApi) {
        STATIC_PLAYBACK_MODULE = audioPlayerApi.registerModuleType(ResourceLocation.fromNamespaceAndPath(AudioPlayerRoleplayMod.MODID, ID), StaticPlaybackModule::new);

        AudioEvents.PLAY_NOTE_BLOCK.register(StaticPlaybackHooks::onPlayback);
        AudioEvents.PLAY_GOAT_HORN.register(StaticPlaybackHooks::onPlayback);
        AudioEvents.PLAY_MUSIC_DISC.register(StaticPlaybackHooks::onPlayback);

        return ID;
    }

    @Override
    public MutableComponent generalUsageInfo() {
        return Component.literal("The sound will play without 3D Audio within the range of the item");
    }

    @Override
    @Nullable
    public MutableComponent itemSpecificInfo(ItemStack itemStack, AudioData audioData) {
        return null;
    }

    @Override
    public MutableComponent moduleName() {
        return Component.literal("Static Sound Mode");
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
        return StaticCommands.class;
    }

    @Override
    public @Nullable Collection<Class<?>> getAdditionalCommandClasses() {
        return null;
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

    }

    @Override
    public @Nullable ModuleKey<?> getModuleKey() {
        return STATIC_PLAYBACK_MODULE;
    }
}
