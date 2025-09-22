package xyz.breadloaf.audioplayerroleplay.audioplayer;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import de.maxhenkel.audioplayer.api.events.AudioEvents;
import de.maxhenkel.audioplayer.api.events.GetSoundIdEvent;
import net.minecraft.resources.ResourceLocation;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.audioplayer.data.StaticPlaybackModule;
import xyz.breadloaf.audioplayerroleplay.audioplayer.data.TestModule;
import xyz.breadloaf.audioplayerroleplay.audioplayer.events.StaticPlaybackHooks;

public class AudioplayerIntegration {

    public static ModuleKey<TestModule> TEST_MODULE;
    public static ModuleKey<StaticPlaybackModule> STATIC_PLAYBACK_MODULE;

    public static void onInitialize() {
        AudioPlayerApi audioPlayerApi = AudioPlayerApi.instance();
        TEST_MODULE = audioPlayerApi.registerModuleType(ResourceLocation.fromNamespaceAndPath(AudioPlayerRoleplayMod.MODID, "test"), TestModule::new);
        STATIC_PLAYBACK_MODULE = audioPlayerApi.registerModuleType(ResourceLocation.fromNamespaceAndPath(AudioPlayerRoleplayMod.MODID, "static"), StaticPlaybackModule::new);

        AudioEvents.GET_SOUND_ID.register(AudioplayerIntegration::getSoundId);

        AudioEvents.PLAY_NOTE_BLOCK.register(StaticPlaybackHooks::onPlayback);
        AudioEvents.PLAY_GOAT_HORN.register(StaticPlaybackHooks::onPlayback);
        AudioEvents.PLAY_MUSIC_DISC.register(StaticPlaybackHooks::onPlayback);
    }

    private static void getSoundId(GetSoundIdEvent event) {
        TestModule testModule = event.getModule(TEST_MODULE).orElse(null);
        if (testModule == null) {
            return;
        }
        //TODO Change the played ID
        //event.setSoundId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        AudioPlayerRoleplayMod.LOGGER.info("Playing sound with test module");
    }

}
