package xyz.breadloaf.audioplayerroleplay.permission;

import de.maxhenkel.admiral.permissions.PermissionManager;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class RoleplayPermissionManager implements PermissionManager<CommandSourceStack> {

    public static final RoleplayPermissionManager INSTANCE = new RoleplayPermissionManager();

    private static final Permission TEST_PERMISSION = new Permission("audioplayer_roleplay.test", PermissionType.OPS);

    private static final List<Permission> PERMISSIONS = List.of(
            TEST_PERMISSION
    );

    @Override
    public boolean hasPermission(CommandSourceStack stack, String permission) {
        for (Permission p : PERMISSIONS) {
            if (!p.permission.equals(permission)) {
                continue;
            }
            if (!p.canUse()) {
                return false;
            }
            if (stack.isPlayer()) {
                return p.hasPermission(stack.getPlayer());
            }
            return stack.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_MODERATOR);
        }
        return false;
    }

    private static Boolean loaded;

    private static boolean isFabricPermissionsAPILoaded() {
        if (loaded == null) {
            loaded = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");
            if (loaded) {
                AudioPlayerRoleplayMod.LOGGER.info("Using Fabric Permissions API");
            }
        }
        return loaded;
    }

    private static class Permission {
        private final String permission;
        private final PermissionType type;

        public Permission(String permission, PermissionType type) {
            this.permission = permission;
            this.type = type;
        }

        public boolean canUse() {
            return true;
        }

        public boolean hasPermission(ServerPlayer player) {
            if (isFabricPermissionsAPILoaded()) {
                return checkFabricPermission(player);
            }
            return type.hasPermission(player);
        }

        private boolean checkFabricPermission(ServerPlayer player) {
            //TODO Add back once permissions API is available for unobf minecraft again
            TriState permissionValue = TriState.DEFAULT;//Permissions.getPermissionValue(player, permission);
            return switch (permissionValue) {
                case DEFAULT -> type.hasPermission(player);
                case TRUE -> true;
                default -> false;
            };
        }

        public PermissionType getType() {
            return type;
        }
    }

    private static enum PermissionType {

        EVERYONE, NOONE, OPS;

        boolean hasPermission(ServerPlayer player) {
            return switch (this) {
                case EVERYONE -> true;
                case NOONE -> false;
                case OPS ->
                        player != null && player.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_OWNER);
            };
        }

    }

}
