package xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations;

import com.google.gson.JsonObject;
import de.maxhenkel.audioplayer.api.data.AudioDataModule;
import xyz.breadloaf.audioplayerroleplay.modules.VirtualLocations.position.Position;

public class VirtualLocationModule implements AudioDataModule {
    Position position;

    public VirtualLocationModule() {
    }
    public VirtualLocationModule(Position position) {
        this.position = position;
    }

    @Override
    public void load(JsonObject dataAccessor) throws Exception {
        position = Position.fromJSON(dataAccessor);
    }

    @Override
    public void save(JsonObject dataModifier) throws Exception {
        position.toJSON(dataModifier);
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
