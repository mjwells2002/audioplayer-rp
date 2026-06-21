package xyz.breadloaf.audioplayerroleplay.modules;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SkullBlock;

import java.util.UUID;

public class ModuleUtils {

    public static MutableComponent getInfoComponent(UUID uuid) {
        return AudioPlayerApi.instance().createInfoMessage(uuid);
    }

    public static boolean isAudioItem(ItemStack itemStack) {
        boolean isAudio = false;
        isAudio |= itemStack.has(DataComponents.JUKEBOX_PLAYABLE);
        Item item = itemStack.getItem();

        isAudio |= item instanceof InstrumentItem;

        if (item instanceof BlockItem blockItem) {
            isAudio |= blockItem.getBlock() instanceof SkullBlock;
        }

        return isAudio;
    }
}
