package xyz.breadloaf.audioplayerroleplay.modules;

import de.maxhenkel.admiral.argumenttype.ArgumentTypeRegistry;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class AudioPlayerDisplayWrapper implements IUserFacingModule {
    @Override
    public String getID() {
        return "default";
    }

    @Override
    public String register(AudioPlayerApi audioPlayerApi) {
        return "default";
    }

    @Override
    public MutableComponent generalUsageInfo() {
        return Component.literal("The base AudioPlayer data");
    }

    @Override
    @Nullable
    public MutableComponent itemSpecificInfo(ItemStack itemStack, AudioData audioData) {
        MutableComponent info = Component.empty();
        info.append(ModuleUtils.getInfoComponent(audioData.getSoundId()));
        if (audioData.getRange() != null) {
            info.append(Component.literal("\nRange " + audioData.getRange()));
        }
        return info;
    }

    @Override
    public MutableComponent moduleName() {
        return Component.literal("Base");
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public boolean canBeDisabled() {
        return false;
    }

    @Override
    public Class<? extends BaseModuleCommand> getCommandClass() {
        return null;
    }

    @Override
    public @Nullable Collection<Class<?>> getAdditionalCommandClasses() {
        return null;
    }

    @Override
    public void earlyRegistrationHook() {

    }

    @Override
    public void registerArgumentTypes(ArgumentTypeRegistry argumentTypeRegistry) {

    }

    @Override
    public void serverStartingHook() {

    }

    @Override
    public void serverStoppingHook() {

    }

    @Override
    public @Nullable ModuleKey<?> getModuleKey() {
        return null;
    }
}
