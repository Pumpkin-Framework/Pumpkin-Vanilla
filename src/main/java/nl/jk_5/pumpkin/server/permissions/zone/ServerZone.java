package nl.jk_5.pumpkin.server.permissions.zone;

import com.google.common.base.Joiner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.util.StringUtils;

import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.permissions.PermissionsList;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseZone;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;

@NonnullByDefault
public class ServerZone extends AbstractZone {

    private static final Logger logger = LogManager.getLogger();

    public ServerZone(DatabaseZone zoneInfo) {
        super(zoneInfo);

        logger.info("Loaded ServerZone:");
        logger.info("Groups: " + StringUtils.join(groups, ", "));
        for (Map.Entry<String, PermissionsList> e : groupPermissions.entrySet()) {
            logger.info(e.getKey() + ": " + e.getValue());
            e.getValue().dump();
        }
        for (Map.Entry<String, Collection<String>> entry : groupMemberships.asMap().entrySet()) {
            logger.info(entry.getKey() + ": " + Joiner.on(',').join(entry.getValue()));
        }
    }

    @Override
    public boolean isInZone(@Nullable Player player, @Nullable User user) {
        return true;
    }
}
