package xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;

public class VirtualLocationConfig {
    public final ConfigEntry<Integer> max_source_to_location_distance;

    public VirtualLocationConfig(ConfigBuilder builder) {
        builder.header("Configuration for Virtual Locations module.");
        max_source_to_location_distance = builder.integerEntry(
                "max_source_to_location_distance",
                -1,
                "sets the max distance from source to virtual location, -1 disables this check"
        );
    }
}
