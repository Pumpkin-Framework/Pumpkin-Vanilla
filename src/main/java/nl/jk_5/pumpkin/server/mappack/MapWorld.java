package nl.jk_5.pumpkin.server.mappack;

import net.minecraft.world.WorldServer;

import nl.jk_5.pumpkin.api.mappack.MappackWorld;
import nl.jk_5.pumpkin.api.mappack.WorldContext;

public class MapWorld {

    private final WorldServer wrapped;
    private final MappackWorld config;

    private Map map;

    public MapWorld(WorldServer wrapped, WorldContext context) {
        this.wrapped = wrapped;
        this.config = context.getConfig();
    }

    public WorldServer getWrapped() {
        return wrapped;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public MappackWorld getConfig() {
        return config;
    }
}
