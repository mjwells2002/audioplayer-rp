package xyz.breadloaf.audioplayerroleplay.mixin;

import com.mojang.serialization.Codec;
import de.maxhenkel.audioplayer.audioloader.AudioData;
import de.maxhenkel.audioplayer.utils.upgrade.ItemUpgrader;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedPlayback;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedSoundModule;
import xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlayback;
import xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlaybackModule;

import java.util.List;
import java.util.UUID;

@Mixin(value = ItemUpgrader.class)
public class UpgradeMixin {

    @Unique
    private static final Codec<List<UUID>> UUID_LIST_CODEC = Codec.list(UUIDUtil.CODEC);

    @Shadow
    @Final
    public static String CUSTOM_SOUND_RANDOM;

    @Shadow
    @Final
    public static String CUSTOM_SOUND_STATIC;

    @Inject(method = "upgradeRoleplayData(Lnet/minecraft/nbt/CompoundTag;Lde/maxhenkel/audioplayer/audioloader/AudioData;)V", at = @At("HEAD"), cancellable = true)
    private static void upgradeItem(CompoundTag tag, AudioData audioData, CallbackInfo ci) {
        List<UUID> randomSounds = tag.read(CUSTOM_SOUND_RANDOM, UUID_LIST_CODEC).orElse(null);
        if (randomSounds != null) {
            audioData.setModule(RandomizedPlayback.RANDOM_PLAYBACK_MODULE, new RandomizedSoundModule(randomSounds));
        }
        boolean staticSound = tag.getBoolean(CUSTOM_SOUND_STATIC).orElse(false);
        if (staticSound) {
            audioData.setModule(StaticPlayback.STATIC_PLAYBACK_MODULE, new StaticPlaybackModule());
        }
        ci.cancel();
    }

    @Inject(method = "upgradeRoleplayData(Lnet/minecraft/world/level/storage/ValueInput;Lde/maxhenkel/audioplayer/audioloader/AudioData;)V", at = @At("HEAD"), cancellable = true)
    private static void upgradeItem(ValueInput valueInput, AudioData audioData, CallbackInfo ci) {
        List<UUID> randomSounds = valueInput.read(CUSTOM_SOUND_RANDOM, UUID_LIST_CODEC).orElse(null);
        if (randomSounds != null) {
            audioData.setModule(RandomizedPlayback.RANDOM_PLAYBACK_MODULE, new RandomizedSoundModule(randomSounds));
        }
        boolean staticSound = valueInput.read(CUSTOM_SOUND_STATIC, Codec.BOOL).orElse(false);
        if (staticSound) {
            audioData.setModule(StaticPlayback.STATIC_PLAYBACK_MODULE, new StaticPlaybackModule());
        }
        ci.cancel();
    }

}
