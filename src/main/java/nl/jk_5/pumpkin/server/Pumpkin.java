package nl.jk_5.pumpkin.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.Driver;

import nl.jk_5.eventbus.Event;
import nl.jk_5.eventbus.EventBus;
import nl.jk_5.pumpkin.api.mappack.DimensionManager;
import nl.jk_5.pumpkin.api.mappack.MappackRegistry;
import nl.jk_5.pumpkin.api.user.UserManager;
import nl.jk_5.pumpkin.server.multiworld.DimensionManagerImpl;
import nl.jk_5.pumpkin.server.multiworld.MapLoader;
import nl.jk_5.pumpkin.server.permissions.PermissionsHandler;
import nl.jk_5.pumpkin.server.player.PlayerManager;
import nl.jk_5.pumpkin.server.sql.SqlMappackRegistry;
import nl.jk_5.pumpkin.server.sql.SqlServiceImpl;
import nl.jk_5.pumpkin.server.sql.SqlTableManager;
import nl.jk_5.pumpkin.server.sql.SqlUserManager;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;
import nl.jk_5.pumpkin.server.util.interfaces.SqlService;
import nl.jk_5.pumpkin.server.web.ServerConnection;
import nl.jk_5.pumpkin.server.web.WebEventHandler;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@NonnullByDefault
public class Pumpkin {

    private static final Pumpkin INSTANCE = new Pumpkin();
    private static final Logger logger = LogManager.getLogger();

    private final MappackRegistry mappackRegistry = new SqlMappackRegistry();
    private final EventBus eventBus = new EventBus();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final MapLoader mapLoader = new MapLoader();
    private final DimensionManagerImpl dimensionManager = new DimensionManagerImpl();
    private final PlayerManager playerManager = new PlayerManager();
    private final PermissionsHandler permissionsHandler = new PermissionsHandler();
    private final UserManager userManager = new SqlUserManager();

    private ServerConnection serverConnection;
    private SqlService sqlService = new SqlServiceImpl();

    public CountDownLatch consoleInitLatch = new CountDownLatch(1);

    public void load(){
        eventBus.register(this.mapLoader);
        eventBus.register(this.playerManager);
        eventBus.register(WebEventHandler.instance());
        eventBus.register(this.permissionsHandler);

        permissionsHandler.register("pumpkin.group.prefix", "");
    }

    public void initialize(){
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            logger.error("Was not able to load sql drivers");
        }

        serverConnection = new ServerConnection();
        serverConnection.connect();

        SqlTableManager.setupTables();

        permissionsHandler.endRegistrations();
    }

    public static Pumpkin instance(){
        return INSTANCE;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public SqlService getSqlService() {
        return sqlService;
    }

    public MappackRegistry getMappackRegistry() {
        return mappackRegistry;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public MapLoader getMapLoader() {
        return mapLoader;
    }

    public DimensionManager getDimensionManager() {
        return dimensionManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public PermissionsHandler getPermissionsHandler() {
        return permissionsHandler;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public <T extends Event> T postEvent(T event){
        return eventBus.post(event);
    }
}
