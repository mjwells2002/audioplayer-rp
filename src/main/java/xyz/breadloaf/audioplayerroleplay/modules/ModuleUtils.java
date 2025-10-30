package xyz.breadloaf.audioplayerroleplay.modules;

import de.maxhenkel.audioplayer.utils.ChatUtils;
import net.minecraft.network.chat.MutableComponent;

import java.util.UUID;

public class ModuleUtils {

    public static MutableComponent getInfoComponent(UUID uuid) {
        return ChatUtils.createInfoMessage(uuid);
    }

}
