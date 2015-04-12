package nl.jk_5.pumpkin.server.multiworld;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import jk_5.eventbus.EventHandler;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.api.mappack.*;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.event.player.PlayerRespawnEvent;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.settings.Settings;
import nl.jk_5.pumpkin.server.util.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public class MapLoader {

    private static final MapLoader INSTANCE = new MapLoader();
    private static final Logger logger = LogManager.getLogger();
    private static final File mapsDir;

    private final AtomicInteger nextId = new AtomicInteger(0);

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

    public ListenableFuture<Map> createMap(final Mappack mappack){
        final SettableFuture<Map> future = SettableFuture.create();
        Pumpkin.instance().getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                final int id = nextId.getAndIncrement();
                final File dir = new File(mapsDir, "map_" + id);
                dir.mkdir();
                try {
                    prepareMappack(mappack, dir);
                } catch (Exception e) {
                    logger.warn("Exception while loading mappack", e);
                    return;
                }
                MinecraftServer.getServer().addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        Map map = new Map(mappack, dir);
                        registerMap(map);
                        loadMappackWorlds(map, mappack, "map_" + id);
                        future.set(map);
                    }
                });
            }
        });
        return future;
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
                    return world.getDimension();
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

    @EventHandler
    public void onRespawn(PlayerRespawnEvent.Pre event){
        event.setRespawnLocation(Location.builder().setWorld(DimensionManagerImpl.instance().getWorld(2)).fromBlockPos(DimensionManagerImpl.instance().getWorld(2).getConfig().getSpawnpoint().toBlockPos()).build());
    }

    public static MapLoader instance(){
        return INSTANCE;
    }
}
