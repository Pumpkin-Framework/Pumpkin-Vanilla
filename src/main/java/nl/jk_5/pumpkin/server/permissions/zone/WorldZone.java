package nl.jk_5.pumpkin.server.permissions.zone;

import com.google.common.base.Joiner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.util.StringUtils;

import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.permissions.PermissionsList;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseZone;

import java.util.Collection;
import javax.annotation.Nullable;

public class WorldZone extends AbstractZone {

    private static final Logger logger = LogManager.getLogger();

    private final int worldId;

    public WorldZone(DatabaseZone zoneInfo) {
        super(zoneInfo);

        worldId = zoneInfo.getWorld().getId();

        logger.info("Loaded WorldZone:");
        logger.info("Groups: " + StringUtils.join(groups, ", "));
        for (java.util.Map.Entry<String, PermissionsList> e : groupPermissions.entrySet()) {
            logger.info(e.getKey() + ": " + e.getValue());
            e.getValue().dump();
        }
        for (java.util.Map.Entry<String, Collection<String>> entry : groupMemberships.asMap().entrySet()) {
            logger.info(entry.getKey() + ": " + Joiner.on(',').join(entry.getValue()));
        }
    }

    @Override
    public boolean isInZone(@Nullable Player player, @Nullable User user) {
        if(player != null && player.isOnline()){
            //noinspection ConstantConditions
            assert player.getWorld() != null;
            return player.getWorld().getConfig().getId() == worldId;
        }else{
            return false;
        }
    }
}
