package nl.jk_5.pumpkin.api.event.player;

import nl.jk_5.pumpkin.server.util.location.Location;

public interface PlayerPreRespawnEvent extends PlayerEvent {

    Location getDeathLocation();

    Location getRespawnLocation();

    void setRespawnLocation(Location respawnLocation);
}
