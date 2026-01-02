package xyz.breadloaf.audioplayerroleplay.modules;

import de.maxhenkel.admiral.argumenttype.ArgumentTypeRegistry;
import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.ModuleKey;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IUserFacingModule {
    String getID();

    String register(AudioPlayerApi audioPlayerApi);

    MutableComponent generalUsageInfo();

    @Nullable MutableComponent itemSpecificInfo(ItemStack itemStack, AudioData audioData);

    MutableComponent moduleName();

    boolean isEnabledByDefault();

    boolean canBeDisabled();

    @Nullable Class<? extends BaseModuleCommand> getCommandClass();

    @Nullable Collection<Class<?>> getAdditionalCommandClasses();

    void earlyRegistrationHook();

    void registerArgumentTypes(ArgumentTypeRegistry argumentTypeRegistry);

    void serverStartingHook();

    void serverStoppingHook();

    @Nullable ModuleKey<?> getModuleKey();
}
