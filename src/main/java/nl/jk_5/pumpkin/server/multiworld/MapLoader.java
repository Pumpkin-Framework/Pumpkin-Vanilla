package nl.jk_5.pumpkin.server.multiworld;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.api.mappack.*;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.settings.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
            logger.fatal("Was not able to load the lobby mappack");
            logger.fatal("No mappack with id {} was found. Shutting down");
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
            logger.warn("Exception while preparing files for lobby mappack", e);
        }
        Map map = new Map(mappack, targetDir);
        if(success){
            this.registerMap(map);
            this.lobby = map;
            try{
                this.loadMappackWorlds(map, mappack, "lobby");
                return map;
            }catch(Exception e){
                logger.fatal("Was not able to load the lobby mappack because of an error", e);
                logger.fatal("Shutting down");
                System.exit(1);
                return null;
            }
        }else{
            logger.fatal("Was not able to load the lobby mappack. Shutting down");
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
                        try{
                            loadMappackWorlds(map, mappack, "map_" + id);
                            future.set(map);
                        }catch(Exception e){
                            future.setException(e);
                        }
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
        logger.info("Downloading files from server for mappack " + mappack.getName());
        //TODO: set trusted certificates
        HttpClient client = HttpClientBuilder.create().disableContentCompression().build();
        for(MappackFile file : mappack.getFiles()){
            if(!file.isRequired()) continue;
            logger.info("Requesting " + file.getPath() + " (" + file.getFileId() + ")");
            HttpGet req = new HttpGet("https://pumpkin.jk-5.nl/api/file/" + file.getFileId());
            HttpResponse res = client.execute(req);
            InputStream content = null;
            FileOutputStream outStream = null;
            try{
                content = res.getEntity().getContent();
                File dest = new File(targetDir, file.getPath());
                dest.getParentFile().mkdirs();
                outStream = new FileOutputStream(dest);
                IOUtils.copy(content, outStream);
            }finally{
                IOUtils.closeQuietly(content);
                IOUtils.closeQuietly(outStream);
            }
            logger.info("File downloaded");
        }
        logger.info("Finished downloading");
    }

    private void loadMappackWorlds(Map map, Mappack mappack, String saveDir) throws Exception {
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

                @Override
                public String getOptions() {
                    if(world.getGenerator().equals("flat")){
                        if(world.getGeneratorOptions().isEmpty()){
                            return null;
                        }
                        return world.getGeneratorOptions();
                    }else{
                        return null;
                    }
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
