package nl.jk_5.pumpkin.api.mappack;

import nl.jk_5.pumpkin.server.util.Location;

public interface MappackWorld {

    Mappack getMappack();

    String getName();

    String getGenerator();

    String getDimension();

    boolean isDefault();

    Location getSpawnpoint();
}
