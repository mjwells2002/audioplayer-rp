package xyz.breadloaf.audioplayerroleplay.config;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import xyz.breadloaf.audioplayerroleplay.modules.IUserFacingModule;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleManager;

import java.util.HashMap;

public class ServerConfig {

    public final HashMap<String, ConfigEntry<Boolean>> enabled_modules;

    public ServerConfig(ConfigBuilder builder) {
        enabled_modules = new HashMap<>();
        for (IUserFacingModule userFacingModule : ModuleManager.MODULES.values()) {
            if (userFacingModule.canBeDisabled()) {
                enabled_modules.put(userFacingModule.getID(), builder.booleanEntry(userFacingModule.getID(), userFacingModule.isEnabledByDefault(), "Enable or disable the %s module".formatted(userFacingModule.getID())));
            }
        }
    }

}
