package nl.jk_5.pumpkin.api.mappack;

import nl.jk_5.pumpkin.server.mappack.MapWorld;

public interface DimensionManager {

    MapWorld register(WorldProvider provider, WorldContext ctx);

    MapWorld getWorld(int id);
}
