package xyz.breadloaf.audioplayerroleplay.voicechat;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.VolumeCategory;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import xyz.breadloaf.audioplayerroleplay.modules.ModuleManager;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Enumeration;

public class RoleplayVoicechatPlugin implements VoicechatPlugin {

    public static final String TEST_CATEGORY_ID = "ap_roleplay_test";

    public static VoicechatApi voicechatApi;
    @Nullable
    public static VoicechatServerApi voicechatServerApi;
//    @Nullable
//    public static VolumeCategory test;

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

//        test = voicechatServerApi.volumeCategoryBuilder()
//                .setId(TEST_CATEGORY_ID)
//                .setName("Test")
//                .setDescription("A test category")
//                .setIcon(getIcon("test_category.png"))
//                .build();



        //voicechatServerApi.registerVolumeCategory(test);
    }

    @Nullable
    private int[][] getIcon(String path) {
        try {
            Enumeration<URL> resources = RoleplayVoicechatPlugin.class.getClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                BufferedImage bufferedImage = ImageIO.read(resources.nextElement().openStream());
                int[][] image = getImageData(bufferedImage);
                if (image != null) return image;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
