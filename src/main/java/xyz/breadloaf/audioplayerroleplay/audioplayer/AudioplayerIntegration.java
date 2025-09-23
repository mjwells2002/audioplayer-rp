package xyz.breadloaf.audioplayerroleplay.audioplayer;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleManager;

public class AudioplayerIntegration {


    public static void onInitialize() {
        AudioPlayerApi audioPlayerApi = AudioPlayerApi.instance();

        ModuleManager.registerAllModuleEvents(audioPlayerApi);
    }

}
