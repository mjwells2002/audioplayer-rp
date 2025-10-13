package xyz.breadloaf.audioplayerroleplay;

import de.maxhenkel.admiral.MinecraftAdmiral;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import net.minecraft.commands.CommandSourceStack;
import xyz.breadloaf.audioplayerroleplay.commands.TestCommands;
import xyz.breadloaf.audioplayerroleplay.config.ServerConfig;
import xyz.breadloaf.audioplayerroleplay.modules.IUserFacingModule;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleManager;
import xyz.breadloaf.audioplayerroleplay.permission.RoleplayPermissionManager;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class AudioPlayerRoleplayMod implements ModInitializer {

    public static final String MODID = "audioplayer_roleplay";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ServerConfig SERVER_CONFIG;

    @Override
    public void onInitialize() {
        SERVER_CONFIG = ConfigBuilder.builder(ServerConfig::new).path(getModConfigFolder().resolve("roleplay-server.properties")).build();
        ModuleManager.configLoadedHook();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            MinecraftAdmiral.Builder<CommandSourceStack> builder = MinecraftAdmiral.builder(dispatcher, registryAccess);
            for (IUserFacingModule userFacingModule : ModuleManager.ENABLED_MODULES.values()) {
                if (userFacingModule.getCommandClass() != null) {
                    builder.addCommandClasses(userFacingModule.getCommandClass());
                }
            }
            builder.addCommandClasses(
                    TestCommands.class
            );
            builder.setPermissionManager(RoleplayPermissionManager.INSTANCE);
            builder.build();
        });
        AudioPlayerApi audioPlayerApi = AudioPlayerApi.instance();

        ModuleManager.registerAllModuleEvents(audioPlayerApi);
    }

    public static Path getModConfigFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve(MODID);
    }

}
