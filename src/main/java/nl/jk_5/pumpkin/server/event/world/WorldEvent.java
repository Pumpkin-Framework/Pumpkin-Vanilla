package nl.jk_5.pumpkin.server.event.world;

import nl.jk_5.eventbus.Event;
import nl.jk_5.pumpkin.server.mappack.MapWorld;

public class WorldEvent extends Event {

    private final MapWorld world;

    public WorldEvent(MapWorld world) {
        this.world = world;
    }

    public MapWorld getWorld() {
        return world;
    }
}
