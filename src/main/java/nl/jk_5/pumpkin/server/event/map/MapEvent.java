package nl.jk_5.pumpkin.server.event.map;

import nl.jk_5.eventbus.Event;
import nl.jk_5.pumpkin.server.mappack.Map;

public class MapEvent extends Event {

    private final Map map;

    public MapEvent(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }
}
