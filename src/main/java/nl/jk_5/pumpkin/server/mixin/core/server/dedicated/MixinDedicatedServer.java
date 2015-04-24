package nl.jk_5.pumpkin.server.mixin.core.server.dedicated;

import net.minecraft.network.rcon.RConThreadMain;
import net.minecraft.network.rcon.RConThreadQuery;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerEula;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.server.dedicated.ServerHangWatchdog;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.CryptManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import nl.jk_5.pumpkin.server.Pumpkin;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;

@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer extends MinecraftServer {

    public MixinDedicatedServer(File workDir, Proxy proxy, File profileCacheDir) {
        super(workDir, proxy, profileCacheDir);
    }

    @Shadow private static Logger logger;
    @Shadow private PropertyManager settings;
    @Shadow private ServerEula eula;
    @Shadow private RConThreadQuery theRConThreadQuery;
    @Shadow private RConThreadMain theRConThreadMain;
    @Shadow public abstract long getMaxTickTime();

    @Inject(method = "startServer", at = @At(value = "INVOKE", target = "Ljava/lang/Runtime;getRuntime()Ljava/lang/Runtime;", shift = At.Shift.BEFORE), cancellable = true)
    protected void onStartServer(CallbackInfoReturnable<Boolean> cb) throws IOException {
        if(Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L){
            logger.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        Pumpkin.instance().load();

        logger.info("Loading properties");
        this.settings = new PropertyManager(new File("server.properties"));
        this.eula = new ServerEula(new File("eula.txt"));

        if(!this.eula.hasAcceptedEULA()) {
            logger.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            this.eula.createEULAFile();

            cb.setReturnValue(false);
        }else{
            if(this.isSinglePlayer()){
                this.setHostname("127.0.0.1");
            }else{
                this.setOnlineMode(this.settings.getBooleanProperty("online-mode", true));
                this.setHostname(this.settings.getStringProperty("server-ip", ""));
            }

            //Settings replaced by per-world values
            this.setCanSpawnAnimals(true);
            this.setCanSpawnNPCs(true);
            this.setAllowPvp(true);
            this.setResourcePack("", "");

            this.setForceGamemode(false);

            //TODO: motd
            this.setMOTD(this.settings.getStringProperty("motd", "A Minecraft Server"));

            this.setPlayerIdleTimeout(this.settings.getIntProperty("player-idle-timeout", 0));
            this.setAllowFlight(this.settings.getBooleanProperty("allow-flight", false));

            InetAddress host = null;
            if(this.getServerHostname().length() > 0){
                host = InetAddress.getByName(this.getServerHostname());
            }

            if(this.getServerPort() < 0){
                this.setServerPort(this.settings.getIntProperty("server-port", 25565));
            }

            logger.info("Generating 1024bit RSA key for encryption of connections");
            this.setKeyPair(CryptManager.generateKeyPair());

            logger.info("Starting Minecraft server on " + (this.getServerHostname().length() == 0 ? "*" : this.getServerHostname()) + ":" + this.getServerPort());

            try{
                this.getNetworkSystem().addLanEndpoint(host, this.getServerPort());
            }catch (IOException ioexception){
                logger.warn("**** FAILED TO BIND TO PORT!");
                logger.warn("The exception was: {}", ioexception.toString());
                logger.warn("Perhaps a server is already running on that port?");
                cb.setReturnValue(false);
                return;
            }

            if(!this.isServerInOnlineMode()){
                logger.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
                logger.warn("The server will make no attempt to authenticate usernames. Beware.");
                logger.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
                logger.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
            }

            ///if(this.convertFiles()){
            ///    this.getPlayerProfileCache().save();
            ///}

            if(!PreYggdrasilConverter.tryConvert(this.settings)){
                cb.setReturnValue(false);
            }else{
                Pumpkin.instance().initialize();

                DedicatedServer server = (DedicatedServer) (Object) this;
                this.setConfigManager(new DedicatedPlayerList(server));
                long startTime = System.nanoTime();

                //TODO: per-world option
                this.setBuildLimit(256);

                this.loadAllWorlds(null, null, 0, null, null); //We overwrite this method, so this is safe

                long endTime = System.nanoTime() - startTime;
                String timeDiff = String.format("%.3fs", (double) endTime / 1E9);
                logger.info("Done (" + timeDiff + ")! For help, type \"help\" or \"?\"");

                Pumpkin.instance().consoleInitLatch.countDown();

                if(this.settings.getBooleanProperty("enable-query", false)){
                    logger.info("Starting GS4 status listener");
                    this.theRConThreadQuery = new RConThreadQuery(server);
                    this.theRConThreadQuery.startThread();
                }

                if(this.settings.getBooleanProperty("enable-rcon", false)){
                    logger.info("Starting remote control listener");
                    this.theRConThreadMain = new RConThreadMain(server);
                    this.theRConThreadMain.startThread();
                }

                if(this.getMaxTickTime() > 0L){
                    Thread watchdog = new Thread(new ServerHangWatchdog(server));
                    watchdog.setName("Server Watchdog");
                    watchdog.setDaemon(true);
                    watchdog.start();
                }

                cb.setReturnValue(true);
            }
        }
    }

    @Overwrite
    public boolean isCommandBlockEnabled(){
        return true;
    }

    @Overwrite
    public boolean isAnnouncingPlayerAchievements(){
        return true;
    }
}
