package xyz.breadloaf.audioplayerroleplay.position;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public class Position {
    @Nullable String posID = null;
    int x = 0;
    int y = 0;
    int z = 0;

    public Position(String id) {
        this.posID = id;
    }

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Nullable
    public Vec3i vec3i() {
        if (this.posID != null) {
            Vec3i loc = PositionManager.POSITIONS.id_to_location.get(this.posID);
            return loc;
        }
        return new Vec3i(this.x, this.y, this.z);
    }

    @Nullable
    public BlockPos blockPos() {
        Vec3i vec3i = vec3i();
        if (vec3i != null) {
            return new BlockPos(vec3i);
        }
        return null;
    }

    public MutableComponent chatComponent() {
        MutableComponent component = Component.empty();
        if (this.posID != null) {
            component.append(Component.literal(posID).withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.CopyToClipboard(posID))));
            component.append(Component.literal(" ("));
        }
        Vec3i vec3i = vec3i();
        if (vec3i != null) {
            component.append(Component.literal("%d, %d, %d".formatted(vec3i.getX(), vec3i.getY(), vec3i.getZ())).withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.CopyToClipboard("%d, %d, %d".formatted(vec3i.getX(), vec3i.getY(), vec3i.getZ())))));
        } else {
            component.append(Component.literal("Location Not Found").withStyle(ChatFormatting.RED));
        }

        if (this.posID != null) {
            component.append(Component.literal(")"));
        }
        return component;
    }
}
