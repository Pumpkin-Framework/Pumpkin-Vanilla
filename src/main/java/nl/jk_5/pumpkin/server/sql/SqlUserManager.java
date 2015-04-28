package nl.jk_5.pumpkin.server.sql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.api.user.UserManager;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseUser;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

@NonnullByDefault
public class SqlUserManager implements UserManager {

    private static final Logger logger = LogManager.getLogger();

    @Override
    @Nullable
    public User getById(int id) {
        try {
            return SqlTableManager.userDao.queryForId(id);
        } catch (SQLException e) {
            logger.error("Error while loading user with id " + id, e);
            return null;
        }
    }

    @Override
    @Nullable
    public User getByUsername(String username) {
        try {
            List<DatabaseUser> res = SqlTableManager.userDao.queryForEq("username", username);
            if(res.isEmpty()){
                return null;
            }
            return res.get(0);
        } catch (SQLException e) {
            logger.error("Error while loading user with username " + username, e);
            return null;
        }
    }

    @Nullable
    @Override
    public User getByMojangId(UUID uuid) {
        try {
            List<DatabaseUser> res = SqlTableManager.userDao.queryForEq("online_mojang_id", uuid.toString());
            if(res.isEmpty()){
                res = SqlTableManager.userDao.queryForEq("offline_mojang_id", uuid.toString());
                if(res.isEmpty()){
                    return null;
                }
            }
            return res.get(0);
        } catch (SQLException e) {
            logger.error("Error while loading user with mojang id " + uuid.toString(), e);
            return null;
        }
    }

    @Override
    public void updateMojangIds(User user){
        try {
            SqlTableManager.userDao.update(((DatabaseUser) user));
        } catch (SQLException e) {
            logger.error("Error while updating mojang id for user with id " + user.getId(), e);
        }
    }
}
