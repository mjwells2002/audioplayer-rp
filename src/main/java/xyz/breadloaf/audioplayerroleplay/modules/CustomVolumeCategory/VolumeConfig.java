package xyz.breadloaf.audioplayerroleplay.modules.CustomVolumeCategory;

import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.StringConfigEntry;
import de.maxhenkel.configbuilder.entry.serializer.StringSerializer;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

public class VolumeConfig extends CommentedPropertyConfig {
    HashMap<String, VolumeCategory> volumeCategories = new HashMap<>();
    public static final Pattern ID_REGEX = Pattern.compile("^[a-z_]{1,16}$");

    public VolumeConfig(Path path) {
        super(new CommentedProperties(false));
        this.path = path;
        reload();
    }

    @Override
    public void reload() {
        super.reload();
        String[] keys = getEntries().keySet().toArray(new String[0]);
        HashMap<String, String> tmp = new HashMap<>(properties);
        properties.clear();
        properties.setHeaderComments(Arrays.asList(
                "Configuration for volume category module.",
                "To add categories add the following 3 keys, id can be a max of 12 letters a-z and _ (no uppercase)",
                "id.name, id.icon (Optional), id.description (Optional)",
                "here is an example, to create a category called example",
                "> # The display name for category id example",
                "> example.name=Example Category",
                "> # (Optional) The icon for category id example",
                "> # suggested format PNG",
                "> # 16x16 pixels required",
                "> example.icon=example icon.png",
                "> # (Optional) The description for category id example",
                "> # displayed on hover in the voicechat volume menu",
                "> example.description=This is an example"));

        for (String key : keys) {
            if (key.endsWith(".name")) {
                String id = key.substring(0,key.length()-".name".length());
                if (id.length() > 12) {
                    System.out.println("Removing invalid volume category from config");
                    continue;
                } else if (!ID_REGEX.matcher(id).matches()) {
                    if (ID_REGEX.matcher(id.toLowerCase()).matches() && tmp.get(id.toLowerCase()+".name") == null) {
                        System.out.println("Warning renaming volume category from " + id + " to " + id.toLowerCase());
                        tmp.put(id.toLowerCase()+".name",tmp.get(id+".name"));
                        tmp.put(id.toLowerCase()+".icon",tmp.getOrDefault(id+".icon",""));
                        tmp.put(id.toLowerCase()+".description",tmp.getOrDefault(id+".description",""));
                        id = id.toLowerCase();
                    } else {
                        System.out.println("Removing invalid volume category from config");
                        continue;
                    }
                }

                this.volumeCategories.put(id,new VolumeCategory(
                        id,
                        stringEntry(id+".name", tmp.get(id+".name"), "","" ,"" ,"The display name for category id "+id),
                        stringEntry(id+".icon", tmp.getOrDefault(id+".icon",""), "(Optional) The icon for category id "+id,
                                "suggested format PNG",
                                "16x16 pixels required"),
                        stringEntry(id+".description", tmp.getOrDefault(id+".description",""), "(Optional) The description for category id "+id,
                                "displayed on hover in the voicechat volume menu")
                ));

            }
        }

        save();
    }

    private StringConfigEntry stringEntry(String key, String def, String... comments) {
        StringConfigEntry stringEntry = new StringConfigEntry(this, StringSerializer.INSTANCE, comments, key, def);
        stringEntry.reload();

        return stringEntry;
    }

    public class VolumeCategory {
        String id;
        StringConfigEntry name;
        StringConfigEntry icon;
        StringConfigEntry description;

        private VolumeCategory(String id, StringConfigEntry name, StringConfigEntry icon, StringConfigEntry description) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.description = description;
        }
    }
}
