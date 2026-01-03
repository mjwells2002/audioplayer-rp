package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import de.maxhenkel.voicechat.api.VolumeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import xyz.breadloaf.audioplayerroleplay.AudioPlayerRoleplayMod;
import xyz.breadloaf.audioplayerroleplay.voicechat.RoleplayVoicechatPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class CategoryManager {
    public static HashMap<String, UserVolumeCategory> CATEGORIES = new HashMap<>();

    public static MutableComponent getChatComponentFor(String id) {
        UserVolumeCategory userVolumeCategory = CATEGORIES.get(id);
        MutableComponent mutableComponent = Component.empty();
        if (userVolumeCategory != null) {
            mutableComponent.append(
                    Component.literal(userVolumeCategory.volumeCategory.getName())
                            .withStyle(ChatFormatting.AQUA)
                            .withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal("ID: " + userVolumeCategory.id)))));
        } else {
            mutableComponent.append(
                    Component.literal("Unknown ID: " + id)
                            .withStyle(ChatFormatting.RED));
        }
        return mutableComponent;
    }

    public static void reloadCategories() {
        for (VolumeConfig.VolumeCategory volumeCategoryData : CustomVolumeCategory.VOLUME_CATEGORIES.volumeCategories.values()) {
            int[][] icon = null;
            String iconPath = volumeCategoryData.icon.get();
            if (iconPath != null && !iconPath.isEmpty()) {
                if (AudioPlayerRoleplayMod.getModuleDataFolder(CustomVolumeCategory.ID) != null) {
                    File path = AudioPlayerRoleplayMod.getModuleDataFolder(CustomVolumeCategory.ID).resolve(iconPath).toFile();
                    if (path.exists()) {
                        try {
                            BufferedImage img = ImageIO.read(path);
                            if (img.getWidth() != 16 || img.getHeight() != 16) {
                                CustomVolumeCategory.LOGGER.error("Ignoring icon image for id: {}, invalid dimensions", volumeCategoryData.id);
                                continue;
                            }
                            icon = RoleplayVoicechatPlugin.getImageData(img);
                        } catch (IOException e) {
                            CustomVolumeCategory.LOGGER.error("Ignoring icon image for id: {}, failed to read file", volumeCategoryData.id);
                        }
                    } else {
                        CustomVolumeCategory.LOGGER.error("Ignoring icon image for id: {}, file does not exist", volumeCategoryData.id);
                    }
                } else {
                    CustomVolumeCategory.LOGGER.error("Attempted to load image before initialization complete");
                }
            }
            UserVolumeCategory category = CATEGORIES.get(volumeCategoryData.id);
            boolean isModified = false;
            if (category != null) {
                isModified |= !Arrays.deepEquals(category.volumeCategory.getIcon(), icon);
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
                String desc = volumeCategoryData.description.get();
                if (desc != null && desc.isEmpty()) {
                    desc = null;
                }
                VolumeCategory volumeCategory = RoleplayVoicechatPlugin.voicechatServerApi.volumeCategoryBuilder()
                        .setId("arp_" + volumeCategoryData.id)
                        .setDescription(desc)
                        .setName(volumeCategoryData.name.get())
                        .setIcon(icon).build();
                if (category != null) {
                    RoleplayVoicechatPlugin.voicechatServerApi.unregisterVolumeCategory(category.volumeCategory);
                }
                RoleplayVoicechatPlugin.voicechatServerApi.registerVolumeCategory(volumeCategory);
                CATEGORIES.put(volumeCategoryData.id, new UserVolumeCategory(volumeCategoryData.id, volumeCategory));
            }
        }
        HashSet<String> removedIds = new HashSet<>(CATEGORIES.keySet());
        removedIds.removeAll(CustomVolumeCategory.VOLUME_CATEGORIES.volumeCategories.keySet());
        for (String id : removedIds) {
            UserVolumeCategory volumeCategory = CATEGORIES.remove(id);
            RoleplayVoicechatPlugin.voicechatServerApi.unregisterVolumeCategory(volumeCategory.volumeCategory);
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
