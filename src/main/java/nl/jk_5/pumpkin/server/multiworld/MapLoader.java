package nl.jk_5.pumpkin.server.multiworld;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.api.mappack.*;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.settings.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;

public class MapLoader {

    private static final MapLoader INSTANCE = new MapLoader();
    private static final Logger logger = LogManager.getLogger();
    private static final File mapsDir;

    static {
        mapsDir = new File("maps");
        if(!mapsDir.isDirectory()){
            mapsDir.mkdirs();
        }
    }

    private final ListMultimap<Map, MapWorld> mapWorlds = ArrayListMultimap.create();
    private final List<Map> maps = new ArrayList<Map>();

    private Map lobby;

    private void registerMap(Map map){
        this.maps.add(map);
        logger.info("Registered {}", map);
    }

    @Nonnull
    public Map createLobby(){
        Mappack mappack = Pumpkin.instance().getMappackRegistry().getById(Settings.lobbyMappack);
        if(mappack == null){
            logger.error("Was not able to load the lobby mappack");
            logger.error("No mappack with id {} was found. Shutting down");
            System.exit(1);
            return null;
        }

        File targetDir = new File(mapsDir, "lobby");
        targetDir.mkdir();
        boolean success = false;
        try{
            prepareMappack(mappack, targetDir);
            success = true;
        }catch(Exception e){
            logger.warn("Exception while loading lobby mappack", e);
        }
        Map map = new Map(mappack, targetDir);
        if(success){
            this.registerMap(map);
            this.lobby = map;
            this.loadMappackWorlds(map, mappack, "lobby");
            return map;
        }else{
            logger.error("Was not able to load the lobby mappack. Shutting down");
            System.exit(1);
            return null;
        }
    }

    public Map getLobby() {
        return lobby;
    }

    private void prepareMappack(Mappack mappack, File targetDir) throws Exception {
        //TODO
    }

    private void loadMappackWorlds(Map map, Mappack mappack, String saveDir){
        for(final MappackWorld world : mappack.getWorlds()){
            WorldProvider provider = new WorldProvider() {
                private int id;

                @Override
                public int getId() {
                    return id;
                }

                @Override
                public void setId(int id) {
                    this.id = id;
                }

                @Nonnull
                @Override
                public Dimension getDimension() {
                    return Dimension.parse(world.getDimension());
                }

                @Nonnull
                @Override
                public String getType() {
                    return world.getGenerator();
                }

                @Nonnull
                @Override
                public String getOptions() {
                    return null;
                }
            };
            map.addWorld(createWorld(provider, new WorldContext(saveDir, world.getName(), world)));
        }
    }

    private MapWorld createWorld(WorldProvider provider, WorldContext ctx){
        int id = DimensionManagerImpl.instance().getNextFreeDimensionId();
        DimensionManagerImpl.instance().registerDimension(id, provider); //TODO: provider
        DimensionManagerImpl.instance().initWorld(id, ctx);
        return DimensionManagerImpl.instance().getWorld(id);
    }

    public void addWorldToMap(MapWorld world, Map map){
        this.mapWorlds.put(map, world);
        world.setMap(map);
        logger.info("World {} was added to {}", world, map);
    }

    public Collection<MapWorld> getWorlds(Map map){
        return this.mapWorlds.get(map);
    }

    public static MapLoader instance(){
        return INSTANCE;
    }
}
