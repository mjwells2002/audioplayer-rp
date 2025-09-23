package xyz.breadloaf.audioplayerroleplay.modules;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.CustomVolumeCategory;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedPlayback;
import xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlayback;

import java.util.HashMap;

public class ModuleManager {

    public static final HashMap<String, IUserFacingModule> MODULES = new HashMap<>();

    static {
        registerModule(new CustomVolumeCategory());
        registerModule(new RandomizedPlayback());
        registerModule(new StaticPlayback());
        registerModule(new AudioPlayerDisplayWrapper());
    }

    public static void registerAllModuleEvents(AudioPlayerApi audioPlayerApi) {
        MODULES.values().forEach(module -> module.register(audioPlayerApi));
    }

    private static void registerModule(IUserFacingModule userFacingModule) {
        MODULES.put(userFacingModule.getID(), userFacingModule);
    }
}
