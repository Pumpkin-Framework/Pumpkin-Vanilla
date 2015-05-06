package nl.jk_5.pumpkin.api.event.map;

import nl.jk_5.pumpkin.api.event.Event;
import nl.jk_5.pumpkin.server.mappack.Map;

public interface MapEvent extends Event {

    Map getMap();
}
