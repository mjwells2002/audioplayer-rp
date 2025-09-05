package xyz.breadloaf.audioplayerroleplay.audioplayer;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import de.maxhenkel.audioplayer.api.events.AudioEvents;
import de.maxhenkel.audioplayer.api.events.GetSoundIdEvent;
import net.minecraft.resources.ResourceLocation;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;

public class AudioplayerIntegration {

    public static ModuleKey<TestModule> TEST_MODULE;

    public static void onInitialize() {
        TEST_MODULE = AudioPlayerApi.instance().registerModuleType(ResourceLocation.fromNamespaceAndPath(AudioPlayerRoleplayMod.MODID, "test"), TestModule::new);
        AudioEvents.GET_SOUND_ID.register(AudioplayerIntegration::getSoundId);
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
