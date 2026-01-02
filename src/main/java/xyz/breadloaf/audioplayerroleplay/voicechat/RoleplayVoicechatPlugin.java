package xyz.breadloaf.audioplayerroleplay.voicechat;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleManager;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;

public class RoleplayVoicechatPlugin implements VoicechatPlugin {

    public static VoicechatApi voicechatApi;
    @Nullable
    public static VoicechatServerApi voicechatServerApi;

    @Override
    public String getPluginId() {
        return "audioplayer_roleplay";
    }

    @Override
    public void initialize(VoicechatApi api) {
        voicechatApi = api;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        voicechatServerApi = event.getVoicechat();
        if (AudioPlayerRoleplayMod.MINECRAFT_SERVER != null) {
            AudioPlayerApi audioPlayerApi = AudioPlayerApi.instance();
            ModuleManager.registerAllModuleEvents(audioPlayerApi);
        }
    }

    @Nullable
    public static int[][] getImageData(BufferedImage bufferedImage) {
        if (bufferedImage.getWidth() != 16 || bufferedImage.getHeight() != 16) {
            return null;
        }
        int[][] image = new int[16][16];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                image[x][y] = bufferedImage.getRGB(x, y);
            }
        }
        return image;
    }

}
