package nl.jk_5.pumpkin.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.Driver;

import nl.jk_5.pumpkin.api.mappack.MappackRegistry;
import nl.jk_5.pumpkin.server.sql.SqlMappackRegistry;
import nl.jk_5.pumpkin.server.sql.SqlServiceImpl;
import nl.jk_5.pumpkin.server.sql.SqlTableManager;
import nl.jk_5.pumpkin.server.util.interfaces.SqlService;
import nl.jk_5.pumpkin.server.web.ServerConnection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Pumpkin {

    private static final Pumpkin INSTANCE = new Pumpkin();
    private static final Logger logger = LogManager.getLogger();

    private final MappackRegistry mappackRegistry = new SqlMappackRegistry();

    private ServerConnection serverConnection;
    private SqlService sqlService = new SqlServiceImpl();

    public void load(){

    }

    public void initialize(){
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            logger.error("Was not able to load sql drivers");
        }

        serverConnection = new ServerConnection();

        SqlTableManager.setupTables();
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
}
