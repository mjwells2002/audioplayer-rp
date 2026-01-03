package xyz.breadloaf.audioplayerroleplay;

import de.maxhenkel.admiral.MinecraftAdmiral;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;
import xyz.breadloaf.audioplayerroleplay.commands.PositionCommands;
import xyz.breadloaf.audioplayerroleplay.commands.InfoCommands;
import xyz.breadloaf.audioplayerroleplay.position.Position;
import xyz.breadloaf.audioplayerroleplay.position.PositionArgument;
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
import xyz.breadloaf.audioplayerroleplay.position.PositionManager;
import xyz.breadloaf.audioplayerroleplay.voicechat.RoleplayVoicechatPlugin;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioPlayerRoleplayMod implements ModInitializer {

    public static final String MODID = "audioplayer_roleplay";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ServerConfig SERVER_CONFIG;
    public static LevelResource AUDIOPLAYER_RP_DATA_DIR = new LevelResource("audioplayer_rp");
    public static ExecutorService SAVE_WORKER = Executors.newFixedThreadPool(1, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("AudioPlayerRP-SaveWorker");
        return t;
    });

    @Nullable
    public static MinecraftServer MINECRAFT_SERVER = null;


    public static Logger getModuleLogger(String id) {
        return LogManager.getLogger(MODID + "_" + id);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing AudioPlayerRoleplayMod on Thread {}", Thread.currentThread().getName());
        SERVER_CONFIG = ConfigBuilder.builder(ServerConfig::new).path(getModConfigFolder().resolve("roleplay-server.properties")).build();
        ModuleManager.configLoadedHook();

        LOGGER.info("Config loaded and modules initialized");
        //ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MODID, "pos"), PositionArgumentType.class, SingletonArgumentInfo.contextFree(PositionArgumentType::region));
        LOGGER.info("Arguments Registered");

        for (IUserFacingModule userFacingModule : ModuleManager.ENABLED_MODULES.values()) {
            userFacingModule.earlyRegistrationHook();
            LOGGER.info("Early registration completed for module: {}", userFacingModule.getID());
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            MinecraftAdmiral.Builder<CommandSourceStack> builder = MinecraftAdmiral.builder(dispatcher, registryAccess);
            builder.addArgumentTypes(argumentTypeRegistry -> {
                for (IUserFacingModule userFacingModule : ModuleManager.ENABLED_MODULES.values()) {
                    userFacingModule.registerArgumentTypes(argumentTypeRegistry);
                }
                argumentTypeRegistry.register(Position.class, new PositionArgument.PositionArgumentSupplier(), new PositionArgument.PositionArgumentTypeConverter());
            });
            for (IUserFacingModule userFacingModule : ModuleManager.ENABLED_MODULES.values()) {
                if (userFacingModule.getCommandClass() != null) {
                    builder.addCommandClasses(userFacingModule.getCommandClass());
                }
                if (userFacingModule.getAdditionalCommandClasses() != null) {
                    builder.addCommandClasses(userFacingModule.getAdditionalCommandClasses());
                }
            }
            builder.addCommandClasses(
                    InfoCommands.class,
                    PositionCommands.class
            );
            builder.setPermissionManager(RoleplayPermissionManager.INSTANCE);
            builder.build();
        });

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);

    }

    private void onServerStarted(MinecraftServer minecraftServer) {
        LOGGER.info("SERVER STARTED");
        MINECRAFT_SERVER = minecraftServer;
        PositionManager.load();
        for (IUserFacingModule userFacingModule : ModuleManager.ENABLED_MODULES.values()) {
            userFacingModule.serverStartingHook();
        }
        if (RoleplayVoicechatPlugin.voicechatServerApi != null) {
            AudioPlayerApi audioPlayerApi = AudioPlayerApi.instance();
            ModuleManager.registerAllModuleEvents(audioPlayerApi);
        }
    }

    private void onServerStopping(MinecraftServer minecraftServer) {
        for (IUserFacingModule userFacingModule : ModuleManager.ENABLED_MODULES.values()) {
            userFacingModule.serverStoppingHook();
        }
        PositionManager.save();
        PositionManager.clear();
        MINECRAFT_SERVER = null;
    }

    public static Path getModConfigFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve(MODID);
    }

    public static Path getModuleConfigFolder(String module_id) {
        return getModConfigFolder().resolve(module_id);
    }

    @Nullable
    public static Path getModuleDataFolder(String module_id) {
        Path path = getModDataFolder();
        if (path == null) {
            return null;
        }
        return path.resolve(module_id);
    }

    @Nullable
    public static Path getModDataFolder() {
        if (MINECRAFT_SERVER == null) {
            return null;
        }
        return MINECRAFT_SERVER.getWorldPath(AUDIOPLAYER_RP_DATA_DIR);
    }
}
