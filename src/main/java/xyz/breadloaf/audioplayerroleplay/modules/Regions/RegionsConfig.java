package xyz.breadloaf.audioplayerroleplay.modules.Regions;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;

public class RegionsConfig {
    public final ConfigEntry<Integer> max_source_to_region_distance;

    public RegionsConfig(ConfigBuilder builder) {
        builder.header("Configuration for regions module.");
        max_source_to_region_distance = builder.integerEntry(
                "max_source_to_region_distance",
                -1,
                "sets the max distance from source to region edge, -1 disables this check"
        );
    }
}
