package nl.jk_5.pumpkin.server.sql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.sql.obj.*;

import java.sql.SQLException;

public class SqlTableManager {

    private static final Logger logger = LogManager.getLogger();

    private static ConnectionSource conn;

    private static Dao<DatabaseGroupPermission, Void> groupPermissionDao;
    private static Dao<DatabaseUserPermission, Void> userPermissionDao;
    private static Dao<DatabaseZone, Integer> zoneDao;
    static Dao<DatabaseMappack, Integer> mappackDao;
    private static Dao<DatabaseMappackAuthor, Integer> mappackAuthorDao;
    private static Dao<DatabaseUser, Integer> userDao;
    private static Dao<DatabaseMappackWorld, Integer> mappackWorldsDao;
    private static Dao<DatabaseMappackFile, Integer> mappackFilesDao;
    private static Dao<DatabaseMappackTeam, Integer> mappackTeamDao;
    private static Dao<DatabaseGamerule, Integer> gameruleDao;

    public static void setupTables(){
        try{
            conn = Pumpkin.instance().getSqlService().getConnectionSource();

            groupPermissionDao = createTable(DatabaseGroupPermission.class);
            userPermissionDao = createTable(DatabaseUserPermission.class);
            zoneDao = createTable(DatabaseZone.class);
            mappackDao = createTable(DatabaseMappack.class);
            mappackAuthorDao = createTable(DatabaseMappackAuthor.class);
            userDao = createTable(DatabaseUser.class);
            mappackWorldsDao = createTable(DatabaseMappackWorld.class);
            mappackFilesDao = createTable(DatabaseMappackFile.class);
            mappackTeamDao = createTable(DatabaseMappackTeam.class);
            gameruleDao = createTable(DatabaseGamerule.class);

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static <T, ID> Dao<T, ID> createTable(Class<T> cls){
        Dao<T, ID> dao = null;
        try{
            dao = DaoManager.createDao(conn, cls);
            //TableUtils.createTableIfNotExists(conn, cls);
        }catch(SQLException e){
            logger.error("Error while creating table for " + cls.getSimpleName());
        }
        return dao;
    }
}
