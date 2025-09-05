package xyz.breadloaf.audioplayerroleplay.voicechat;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.VolumeCategory;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;

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
    @Nullable
    public static VolumeCategory test;

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
        test = voicechatServerApi.volumeCategoryBuilder()
                .setId(TEST_CATEGORY_ID)
                .setName("Test")
                .setDescription("A test category")
                .setIcon(getIcon("test_category.png"))
                .build();

        voicechatServerApi.registerVolumeCategory(test);
    }

    @Nullable
    private int[][] getIcon(String path) {
        try {
            Enumeration<URL> resources = RoleplayVoicechatPlugin.class.getClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                BufferedImage bufferedImage = ImageIO.read(resources.nextElement().openStream());
                if (bufferedImage.getWidth() != 16) {
                    continue;
                }
                if (bufferedImage.getHeight() != 16) {
                    continue;
                }
                int[][] image = new int[16][16];
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    for (int y = 0; y < bufferedImage.getHeight(); y++) {
                        image[x][y] = bufferedImage.getRGB(x, y);
                    }
                }
                return image;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
