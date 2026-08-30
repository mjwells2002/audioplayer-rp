package xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations.position;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;
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

    public static Position fromJSON(JsonObject dataAccessor) {
        if (dataAccessor.get("pos").isJsonPrimitive()) {
            String s = dataAccessor.get("pos").getAsString();
            return new Position(s);
        } else {
            JsonArray js = dataAccessor.get("pos").getAsJsonArray();
            return new Position(js.get(0).getAsInt(),js.get(1).getAsInt(),js.get(2).getAsInt());
        }
    }

    public void toJSON(JsonObject dataModifier) {
        if (this.posID != null) {
            dataModifier.add("pos",new JsonPrimitive(posID));
        } else {
            JsonArray js = new JsonArray(3);
            js.set(0, new JsonPrimitive(this.x));
            js.set(1, new JsonPrimitive(this.y));
            js.set(2, new JsonPrimitive(this.z));

            dataModifier.add("pos", js);
        }
    }

    @Nullable
    public Vec3i vec3i() {
        if (this.posID != null) {
            return PositionManager.POSITIONS.id_to_location.get(this.posID);
        }
        return new Vec3i(this.x, this.y, this.z);
    }

    @Nullable
    public Vec3 vec3() {
        if (this.posID != null) {
            Vec3i loc = PositionManager.POSITIONS.id_to_location.get(this.posID);
            return new Vec3(loc);
        }
        return new Vec3(this.x, this.y, this.z);
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

    @Override
    public String toString() {
        return "%d %d %d (id: %s)".formatted(x,y,z,posID == null ? "" : posID);
    }
}
