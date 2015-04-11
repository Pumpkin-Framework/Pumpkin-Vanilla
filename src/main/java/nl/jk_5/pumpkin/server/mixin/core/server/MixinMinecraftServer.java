package nl.jk_5.pumpkin.server.mixin.core.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.command.ICommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Bootstrap;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import nl.jk_5.pumpkin.api.mappack.WorldContext;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.multiworld.DimensionManagerImpl;
import nl.jk_5.pumpkin.server.multiworld.MapLoader;
import nl.jk_5.pumpkin.server.util.ShutdownThread;

import java.io.File;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.FutureTask;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer extends MinecraftServer {

    @Shadow private static Logger logger;
    @Shadow private static MinecraftServer mcServer;
    @Shadow private boolean worldIsBeingDeleted;
    @Shadow private WorldServer[] worldServers;
    @Shadow private int tickCounter;
    @Shadow private List<IUpdatePlayerListBox> playersOnline;
    @Shadow private long[][] timeOfLastDimensionTick;
    @Shadow private Proxy serverProxy;
    @Shadow private File anvilFile;
    @Shadow private NetworkSystem networkSystem;
    @Shadow private PlayerProfileCache profileCache;
    @Shadow private ICommandManager commandManager;
    @Shadow private ISaveFormat anvilConverterForAnvilFile;
    @Shadow private YggdrasilAuthenticationService authService;
    @Shadow private MinecraftSessionService sessionService;
    @Shadow private GameProfileRepository profileRepo;

    public MixinMinecraftServer(File workDir, Proxy proxy, File profileCacheDir) {
        super(workDir, proxy, profileCacheDir);
        this.worldServers = new WorldServer[0];
    }

    @Overwrite
    protected void loadAllWorlds(String p_71247_1_, String p_71247_2_, long seed, WorldType type, String p_71247_6_) {
        this.setUserMessage("menu.loadingLevel");
        Map map = MapLoader.instance().createLobby();
        MapWorld world = map.getPrimaryWorld();
        WorldServer worldServer = world.getWrapped();
        this.getConfigurationManager().setPlayerManager(new WorldServer[]{worldServer});

        //Normally, initialWorldChunkLoad would be called here to preload the spawn chunks
        //this.initialWorldChunkLoad();
        //Because we do some modifications, i copied the entire method in this one, as this is the only place it's used

        this.setUserMessage("menu.generatingTerrain");
        logger.info("Preloading spawn chunks for lobby world");
        //TODO: maybe obtain spawnpoint from map configuration
        BlockPos spawnPoint = worldServer.getSpawnPoint();
        long lastProgress = getCurrentTimeMillis();

        int chunksLoaded = 0;
        for(int x = -192; x <= 192 && this.isServerRunning(); x += 16) {
            for(int z = -192; z <= 192 && this.isServerRunning(); z += 16) {
                long currentTime = getCurrentTimeMillis();

                if(currentTime - lastProgress > 1000L) {
                    this.outputPercentRemaining("Preparing spawn area", chunksLoaded * 100 / 625);
                    lastProgress = currentTime;
                }

                chunksLoaded ++;
                worldServer.theChunkProviderServer.loadChunk(spawnPoint.getX() + x >> 4, spawnPoint.getZ() + z >> 4);
            }
        }

        this.clearCurrentTask();
    }

    @Inject(method = "stopServer()V", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
    public void unregisterAllWorlds(CallbackInfo info){
        if(!this.worldIsBeingDeleted) {
            for(WorldServer world : worldServers){
                DimensionManagerImpl.instance().setWorld(world.provider.getDimensionId(), null);
            }
        }
    }

    @Overwrite
    public void updateTimeLightAndEntities(){
        this.theProfiler.startSection("jobs");

        synchronized(this.futureTaskQueue){
            while(!this.futureTaskQueue.isEmpty()){
                try{
                    ((FutureTask)this.futureTaskQueue.poll()).run();
                }catch (Throwable throwable2){
                    logger.fatal(throwable2);
                }
            }
        }

        this.theProfiler.endStartSection("levels");

        int[] ids = DimensionManagerImpl.instance().getAllDimensionIds();
        for (int x = 0; x < ids.length; x++){
            int id = ids[x];
            long i = System.nanoTime();

            if(id == 0 || this.getAllowNether()){ //TODO: always allow other worlds?
                WorldServer worldserver = DimensionManagerImpl.instance().getWorld(id).getWrapped();
                this.theProfiler.startSection(worldserver.getWorldInfo().getWorldName());

                if(this.tickCounter % 20 == 0){
                    this.theProfiler.startSection("timeSync");
                    this.getConfigurationManager().sendPacketToAllPlayersInDimension(new S03PacketTimeUpdate(worldserver.getTotalWorldTime(), worldserver.getWorldTime(), worldserver.getGameRules().getGameRuleBooleanValue("doDaylightCycle")), worldserver.provider.getDimensionId());
                    this.theProfiler.endSection();
                }

                this.theProfiler.startSection("tick");
                //TODO: pretick here

                try{
                    worldserver.tick();
                }catch(Throwable t){
                    //TODO: Maybe make this not crash?
                    CrashReport report = CrashReport.makeCrashReport(t, "Exception ticking world");
                    worldserver.addWorldInfoToCrashReport(report);
                    throw new ReportedException(report);
                }

                try{
                    worldserver.updateEntities();
                }catch(Throwable t){
                    //TODO: Maybe make this not crash?
                    CrashReport report = CrashReport.makeCrashReport(t, "Exception ticking world entities");
                    worldserver.addWorldInfoToCrashReport(report);
                    throw new ReportedException(report);
                }

                //TODO: posttick here

                this.theProfiler.endSection();
                this.theProfiler.startSection("tracker");
                worldserver.getEntityTracker().updateTrackedEntities();
                this.theProfiler.endSection();
                this.theProfiler.endSection();
            }

            DimensionManagerImpl.instance().worldTickTimes.get(id)[this.tickCounter % 100] = System.nanoTime() - i;
        }

        this.theProfiler.endStartSection("pumpkin:dimensionUnloading");
        DimensionManagerImpl.instance().unloadWorlds();
        this.theProfiler.endStartSection("connection");
        this.getNetworkSystem().networkTick();
        this.theProfiler.endStartSection("players");
        this.getConfigurationManager().onTick();
        this.theProfiler.endStartSection("tickables");

        for(int j = 0; j < this.playersOnline.size(); ++j){
            this.playersOnline.get(j).update();
        }

        this.theProfiler.endSection();
    }

    @Overwrite
    public WorldServer worldServerForDimension(int dimension) {
        WorldServer world = DimensionManagerImpl.instance().getWorld(dimension).getWrapped();
        if(world == null){
            DimensionManagerImpl.instance().initWorld(dimension, new WorldContext(null, null, null));
            world = DimensionManagerImpl.instance().getWorld(dimension).getWrapped();
        }
        return world;
    }

    @Overwrite
    public static void main(String[] args) {
        Bootstrap.register();

        DedicatedServer server = new DedicatedServer(new File("maps"));

        server.startServerThread();

        Runtime.getRuntime().addShutdownHook(new ShutdownThread(server));
    }

    @Overwrite
    public String getServerModName(){
        return "pumpkin";
    }
}