package xyz.breadloaf.audioplayerroleplay;

import com.llamalad7.mixinextras.lib.grammar.expressions.ExpressionParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.maxhenkel.admiral.MinecraftAdmiral;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.voicechat.api.VolumeCategory;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.commands.synchronization.brigadier.StringArgumentSerializer;
import net.minecraft.resources.ResourceLocation;
import xyz.breadloaf.audioplayerroleplay.commands.TestCommands;
import xyz.breadloaf.audioplayerroleplay.config.ServerConfig;
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.CategoryManager;
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.VolumeCategoryArgumentType;
import xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.VolumeConfig;
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

    public static Logger getModuleLogger(String id) {
        return LogManager.getLogger(MODID+"_"+id);
    }

    @Override
    public void onInitialize() {
        SERVER_CONFIG = ConfigBuilder.builder(ServerConfig::new).path(getModConfigFolder().resolve("roleplay-server.properties")).build();
        ModuleManager.configLoadedHook();

        for (IUserFacingModule userFacingModule : ModuleManager.ENABLED_MODULES.values()) {
            userFacingModule.earlyRegistrationHook();
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            MinecraftAdmiral.Builder<CommandSourceStack> builder = MinecraftAdmiral.builder(dispatcher, registryAccess);
            builder.addArgumentTypes(argumentTypeRegistry -> {
                for (IUserFacingModule userFacingModule : ModuleManager.ENABLED_MODULES.values()) {
                    userFacingModule.registerArgumentTypes(argumentTypeRegistry);
                }
            });
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


    }

    public static Path getModConfigFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve(MODID);
    }

    public static Path getModuleConfigFolder(String module_id) {
        return getModConfigFolder().resolve(module_id);
    }

}
