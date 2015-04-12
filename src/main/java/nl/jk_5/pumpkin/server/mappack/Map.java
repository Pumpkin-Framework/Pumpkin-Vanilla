package nl.jk_5.pumpkin.server.mappack;

import nl.jk_5.pumpkin.api.mappack.Mappack;
import nl.jk_5.pumpkin.server.multiworld.MapLoader;

import java.io.File;
import java.util.Collection;

public class Map {

    private final Mappack mappack;
    private final File dir;
    private final String internalName;

    private MapWorld defaultWorld;

    public Map(Mappack mappack, File dir) {
        this.mappack = mappack;
        this.dir = dir;
        this.internalName = dir.getName();
    }

    public MapWorld getPrimaryWorld(){
        return this.defaultWorld;
    }

    public Collection<MapWorld> getWorlds(){
        return MapLoader.instance().getWorlds(this);
    }

    public void addWorld(MapWorld world) {
        MapLoader.instance().addWorldToMap(world, this);
        if(world.getConfig().isDefault()){
            defaultWorld = world;
        }
    }

    public String getInternalName() {
        return internalName;
    }
}
