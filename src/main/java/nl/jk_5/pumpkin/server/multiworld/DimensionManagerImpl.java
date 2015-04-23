package nl.jk_5.pumpkin.server.multiworld;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.api.mappack.DimensionManager;
import nl.jk_5.pumpkin.api.mappack.WorldContext;
import nl.jk_5.pumpkin.api.mappack.WorldProvider;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.mixin.interfaces.IWorldProvider;
import nl.jk_5.pumpkin.server.util.WorldInfoHelper;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public final class DimensionManagerImpl implements DimensionManager {

    private static final Logger logger = LogManager.getLogger();

    private final TIntObjectMap<WorldProvider> customProviders = new TIntObjectHashMap<WorldProvider>();
    private final TIntObjectMap<MapWorld> worlds = new TIntObjectHashMap<MapWorld>();
    private final TIntObjectMap<WorldContext> worldContext = new TIntObjectHashMap<WorldContext>();
    private final TIntList dimensions = new TIntArrayList();
    private final TIntList unloadQueue = new TIntArrayList();
    private final BitSet dimensionMap = new BitSet(java.lang.Long.SIZE << 4);

    public final TIntObjectMap<long[]> worldTickTimes = new TIntObjectHashMap<long[]>();

    public DimensionManagerImpl() {
        this.dimensionMap.set(1);
    }

    public void unregisterDimension(int id) {
        if (!dimensions.contains(id)) {
            throw new IllegalArgumentException("Failed to unregister dimension for id " + id + ". No provider registered");
        }
        dimensions.remove(id);
    }

    @Override
    public MapWorld register(WorldProvider provider, WorldContext ctx) {
        int id = getNextFreeDimensionId();
        if(this.dimensions.contains(id)){
            throw new IllegalArgumentException("Failed to register dimension for id " + id + ". One is already registered");
        }
        customProviders.put(id, provider);
        dimensions.add(id);
        if(id >= 0){
            dimensionMap.set(id);
        }
        if(!this.dimensions.contains(id) && !this.customProviders.containsKey(id)){
            throw new IllegalArgumentException("Provider type for dimension " + id + " does not exist!");
        }
        MinecraftServer mcserver = MinecraftServer.getServer();
        String name = ctx.getName() + "/" + ctx.getSubName();
        ISaveHandler saveHandler = mcserver.getActiveAnvilConverter().getSaveLoader(name, true);

        WorldInfo worldInfo = new WorldInfo();
        WorldInfoHelper.apply(worldInfo, ctx.getConfig());
        logger.info(worldInfo.getNBTTagCompound().toString());

        worldContext.put(id, ctx);
        WorldServer world = new WorldServer(mcserver, saveHandler, worldInfo, id, mcserver.theProfiler);
        this.setWorld(id, world);
        world.init();
        world.addWorldAccess(new WorldManager(mcserver, world));

        //NailedEventFactory.fireWorldLoad(world);
        return this.worlds.get(id);
    }

    @Override
    public MapWorld getWorld(int dimension){
        return this.worlds.get(dimension);
    }

    public void unloadWorld(int id){
        this.unloadQueue.add(id);
    }

    //Private api
    public net.minecraft.world.WorldProvider createProviderFor(int dim){
        if(!this.customProviders.containsKey(dim)){
            throw new RuntimeException("No WorldProvider bound for dimension " + dim);
        }
        DelegatingWorldProvider d = new DelegatingWorldProvider(this.customProviders.get(dim));
        ((IWorldProvider) d).setDimensionId(dim);
        return d;
    }

    public void unloadWorlds(){
        TIntIterator it = this.unloadQueue.iterator();
        while(it.hasNext()){
            int id = it.next();
            if(worlds.containsKey(id)){
                WorldServer w = worlds.get(id).getWrapped();
                try{
                    w.saveAllChunks(true, null);
                }catch(MinecraftException e){
                    logger.warn("Error while unloading world " + id, e);
                }finally{
                    //NailedEventFactory.fireWorldUnload(w);
                    w.flush();
                    setWorld(id, null);
                }
                it.remove();
            }
        }
    }

    private int getNextFreeDimensionId(){
        int next = 0;
        while(true){
            next = this.dimensionMap.nextClearBit(next);
            if(dimensions.contains(next)){
                dimensionMap.set(next);
            }else{
                return next;
            }
        }
    }

    public boolean isDimensionRegistered(int dim) {
        return dimensions.contains(dim);
    }

    public int[] getAllDimensionIds() {
        return this.worlds.keys();
    }

    public void setWorld(int id, WorldServer world) {
        WorldContext context = this.worldContext.get(id);
        if(world != null){
            MapWorld nworld = new MapWorld(world, context);
            this.worlds.put(id, nworld);
            worldTickTimes.put(id, new long[100]);
            logger.info("Loading dimension " + id + " (" + world.getWorldInfo().getWorldName() + ") (" + nworld.toString() + ")");
        }else{
            this.worlds.remove(id);
            worldContext.remove(id);
            logger.info("Unloading dimension " + id);
        }
        final List<WorldServer> builder = new ArrayList<WorldServer>();
        if(this.worlds.containsKey(0)){
            builder.add(worlds.get(0).getWrapped());
        }
        this.worlds.forEachEntry(new TIntObjectProcedure<MapWorld>() {
            @Override
            public boolean execute(int dim, MapWorld world) {
                if(dim < -1 || dim > 1){
                    builder.add(world.getWrapped());
                }
                return true;
            }
        });
        MinecraftServer.getServer().worldServers = builder.toArray(new WorldServer[builder.size()]);
    }
}
