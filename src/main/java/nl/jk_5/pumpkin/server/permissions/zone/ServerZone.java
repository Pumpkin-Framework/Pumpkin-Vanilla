package nl.jk_5.pumpkin.server.permissions.zone;

import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseZone;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public class ServerZone extends AbstractZone {

    public ServerZone(DatabaseZone zoneInfo) {
        super(zoneInfo);
    }

    @Override
    public boolean isInZone(@Nullable Player player, @Nullable User user) {
        return true;
    }
}
