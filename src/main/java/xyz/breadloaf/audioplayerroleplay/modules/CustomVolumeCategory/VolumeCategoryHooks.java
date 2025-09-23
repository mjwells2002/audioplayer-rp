package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.events.PostPlayEvent;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;

import static xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory.CustomVolumeCategory.CUSTOM_VOLUME_CATEGORY_MODULE;
import static xyz.breadloaf.audioplayerroleplay.modules.StaticPlayback.StaticPlayback.STATIC_PLAYBACK_MODULE;
import static xyz.breadloaf.audioplayerroleplay.voicechat.RoleplayVoicechatPlugin.TEST_CATEGORY_ID;

public class VolumeCategoryHooks {
    public static void onPostPlay(PostPlayEvent postPlayEvent) {
        AudioPlayerApi audioPlayerApi = AudioPlayerApi.instance();
        if (postPlayEvent.getData().getModule(CUSTOM_VOLUME_CATEGORY_MODULE).isPresent()) {
            AudioChannel channel = postPlayEvent.getChannel().getChannel();
            channel.setCategory(TEST_CATEGORY_ID);
        }
    }
}
