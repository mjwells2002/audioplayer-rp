package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import de.maxhenkel.voicechat.api.VolumeCategory;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.voicechat.RoleplayVoicechatPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class CategoryManager {
    public static HashMap<String, UserVolumeCategory> CATEGORIES = new HashMap<>();

    public static void reloadCategories() {
        for (VolumeConfig.VolumeCategory volumeCategoryData : CustomVolumeCategory.VOLUME_CATEGORIES.volumeCategories.values()) {
            int[][] icon = null;
            String iconPath = volumeCategoryData.icon.get();
            if (iconPath != null) {
                File path = AudioPlayerRoleplayMod.getModuleConfigFolder(CustomVolumeCategory.ID).resolve(iconPath).toFile();
                if (path.exists()) {
                    try {
                        BufferedImage img = ImageIO.read(path);
                        if (img.getWidth() != 16 || img.getHeight() != 16) {
                            System.out.println("Ignoring icon image invalid dimensions");
                            continue;
                        }
                        icon = RoleplayVoicechatPlugin.getImageData(img);
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                } else {
                    System.out.println("icon doesnt exist");
                }
            }
            UserVolumeCategory category = CATEGORIES.get(volumeCategoryData.id);
            boolean isModified = false;
            if (category != null) {
                isModified |= !Arrays.deepEquals(category.volumeCategory.getIcon(),icon);
                isModified |= !category.volumeCategory.getName().equals(volumeCategoryData.name.get());
                if (category.volumeCategory.getDescription() != null) {
                    isModified |= !category.volumeCategory.getDescription().equals(volumeCategoryData.description.get());
                } else if (volumeCategoryData.description.get() != null && !volumeCategoryData.description.get().isEmpty()) {
                    isModified = true;
                }
            } else {
                isModified = true;
            }
            if (isModified) {
                VolumeCategory volumeCategory = RoleplayVoicechatPlugin.voicechatServerApi.volumeCategoryBuilder()
                        .setId("arp_" + volumeCategoryData.id)
                        .setDescription(volumeCategoryData.description.get())
                        .setName(volumeCategoryData.name.get())
                        .setIcon(icon).build();
                if (category != null) {
                    RoleplayVoicechatPlugin.voicechatServerApi.unregisterVolumeCategory(category.volumeCategory);
                }
                RoleplayVoicechatPlugin.voicechatServerApi.registerVolumeCategory(volumeCategory);
                CATEGORIES.put(volumeCategoryData.id, new UserVolumeCategory(volumeCategoryData.id, volumeCategory));
            }
        }
    }

    public static class UserVolumeCategory {
        String id;
        VolumeCategory volumeCategory;
        public UserVolumeCategory(String id, VolumeCategory volumeCategory) {
            this.id = id;
            this.volumeCategory = volumeCategory;
        }
    }
}
