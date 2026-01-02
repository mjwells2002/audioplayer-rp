package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@Command({"roleplay", "regions", "named"})
public class NamedRegionCommands {
    @Command("set")
    public void createRegion(CommandContext<CommandSourceStack> context, @Name("region id") String region_id, @Name("pos 1") BlockPos pos1, @Name("pos 2") BlockPos pos2) throws CommandSyntaxException {
        updateRegionInternal(context,region_id,pos1,pos2,true);
    }

    @Command("set")
    public void updateRegion(CommandContext<CommandSourceStack> context, @Name("region") Region region, @Name("pos 1") BlockPos pos1, @Name("pos 2") BlockPos pos2) throws CommandSyntaxException {
        updateRegionInternal(context,region.id,pos1,pos2,false);
    }

    private void updateRegionInternal(CommandContext<CommandSourceStack> context, String region_id, BlockPos pos1, BlockPos pos2, boolean created) {
        RegionManager.setNamedRegion(region_id, pos1, pos2);
        MutableComponent component = Component.empty();
        if (created) {
            component.append("Region Created:\n").withStyle(ChatFormatting.AQUA);
        } else {
            component.append("Region Updated:\n").withStyle(ChatFormatting.AQUA);
        }
        component.append(new Region(region_id).chatComponent());
        context.getSource().sendSuccess(() -> component, false);
    }

    @Command("list")
    public void listRegions(CommandContext<CommandSourceStack> context) {
        MutableComponent component = Component.empty();
        component.append("Regions:\n").withStyle(ChatFormatting.AQUA);
        if (RegionManager.REGIONS != null) {
            RegionManager.REGIONS.id_to_minmax.keySet().forEach(id -> {
                component.append(new Region(id).chatComponent());
                component.append("\n");
            });
        }
        context.getSource().sendSuccess(() -> component, false);
    }
}
