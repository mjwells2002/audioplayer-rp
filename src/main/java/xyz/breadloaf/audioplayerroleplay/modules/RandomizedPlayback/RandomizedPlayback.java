package xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback;

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
import xyz.breadloaf.audioplayerroleplay.modules.ModuleUtils;

import java.util.UUID;

public class RandomizedPlayback implements IUserFacingModule  {
    public static ModuleKey<RandomizedSoundModule> RANDOM_PLAYBACK_MODULE;
    static final String ID = "rng_playback";

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String register(AudioPlayerApi audioPlayerApi) {
        RANDOM_PLAYBACK_MODULE = audioPlayerApi.registerModuleType(ResourceLocation.fromNamespaceAndPath(AudioPlayerRoleplayMod.MODID, ID), RandomizedSoundModule::new);

        AudioEvents.GET_SOUND_ID.register(RandomizedPlaybackHooks::onGetSoundId);

        return ID;
    }

    @Override
    public MutableComponent generalUsageInfo() {
        return Component.literal("Randomizes the sound played by this item picks a new sound randomly every playback, this overrides the sound from base module");
    }

    @Override
    @Nullable
    public MutableComponent itemSpecificInfo(ItemStack itemStack, AudioData audioData) {
        MutableComponent info = Component.empty();
        RandomizedSoundModule data = audioData.getModule(RANDOM_PLAYBACK_MODULE).orElse(null);
        if (data != null) {
            for (UUID uuid : data.soundIds) {
                info.append(ModuleUtils.getInfoComponent(uuid));
                info.append(Component.literal("\n"));
            }
        }
        return info;
    }

    @Override
    public MutableComponent moduleName() {
        return Component.literal("Randomized Sound Playback");
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
        return RandomizedPlaybackCommands.class;
    }

    @Override
    public @Nullable ModuleKey<?> getModuleKey() {
        return RANDOM_PLAYBACK_MODULE;
    }
}
