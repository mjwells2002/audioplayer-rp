package xyz.breadloaf.audioplayerroleplay.config;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;

public class ServerConfig {

    public final ConfigEntry<String> test;

    public ServerConfig(ConfigBuilder builder) {
        test = builder.stringEntry(
                "test",
                "",
                "Just a test"
        );
    }

}
