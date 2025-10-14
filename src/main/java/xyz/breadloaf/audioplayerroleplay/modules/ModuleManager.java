package xyz.breadloaf.audioplayerroleplay.modules;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.CustomVolumeCategory;
import xyz.breadloaf.audioplayerroleplay.modules.RandomizedPlayback.RandomizedPlayback;
import xyz.breadloaf.audioplayerroleplay.modules.Regions.RegionsModule;
import xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlayback;

import java.util.HashMap;

public class ModuleManager {
    public static final HashMap<String, IUserFacingModule> ENABLED_MODULES = new HashMap<>();
    public static final HashMap<String, IUserFacingModule> MODULES = new HashMap<>();

    static {
        registerModule(new RegionsModule());
        registerModule(new CustomVolumeCategory());
        registerModule(new RandomizedPlayback());
        registerModule(new StaticPlayback());
        registerModule(new AudioPlayerDisplayWrapper());
    }

    public static void configLoadedHook() {
        for (IUserFacingModule userFacingModule : MODULES.values()) {
            if (userFacingModule.canBeDisabled()) {
                if (!AudioPlayerRoleplayMod.SERVER_CONFIG.enabled_modules.get(userFacingModule.getID()).get()) {
                    continue;
                }
            }
            AudioPlayerRoleplayMod.LOGGER.info("module {} is enabled", userFacingModule.getID());
            ENABLED_MODULES.put(userFacingModule.getID(), userFacingModule);
        }
    }

    public static void registerAllModuleEvents(AudioPlayerApi audioPlayerApi) {
        ENABLED_MODULES.values().forEach(module -> module.register(audioPlayerApi));
    }

    private static void registerModule(IUserFacingModule userFacingModule) {
        MODULES.put(userFacingModule.getID(), userFacingModule);
    }
}
