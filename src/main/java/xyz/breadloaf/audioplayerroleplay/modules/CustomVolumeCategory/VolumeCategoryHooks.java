package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import de.maxhenkel.audioplayer.api.events.PostPlayEvent;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;

import java.util.Optional;

import static xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.CustomVolumeCategory.CUSTOM_VOLUME_CATEGORY_MODULE;

public class VolumeCategoryHooks {

    public static void onPostPlay(PostPlayEvent postPlayEvent) {
        Optional<VolumeCategoryModule> volumeCategoryModule = postPlayEvent.getData().getModule(CUSTOM_VOLUME_CATEGORY_MODULE);
        if (volumeCategoryModule.isPresent()) {
            VolumeCategoryModule module = volumeCategoryModule.get();
            if (module.id != null && CategoryManager.CATEGORIES.get(module.id) != null) {
                AudioChannel channel = postPlayEvent.getChannel().getChannel();
                channel.setCategory(CategoryManager.CATEGORIES.get(module.id).volumeCategory.getId());
            }
        }
    }

}
