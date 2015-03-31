package nl.jk_5.pumpkin.server.sql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.api.mappack.Mappack;
import nl.jk_5.pumpkin.api.mappack.MappackRegistry;

import java.sql.SQLException;

public class SqlMappackRegistry implements MappackRegistry {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public Mappack getById(int id) {
        try {
            return SqlTableManager.mappackDao.queryForId(id);
        } catch (SQLException e) {
            logger.error("Error while loading mappack with id " + id, e);
            return null;
        }
    }
}
