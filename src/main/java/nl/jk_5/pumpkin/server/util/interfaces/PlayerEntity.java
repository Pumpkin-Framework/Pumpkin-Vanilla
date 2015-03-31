package nl.jk_5.pumpkin.server.util.interfaces;

import nl.jk_5.pumpkin.server.permission.api.PlayerIdentity;
import nl.jk_5.pumpkin.server.util.Location;

public interface PlayerEntity extends PlayerIdentity {

    Location getLocation();
}
