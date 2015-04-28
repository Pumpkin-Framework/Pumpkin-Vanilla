package nl.jk_5.pumpkin.server.permissions.zone;

import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public interface Zone {

    @Nullable
    String getPermissionValue(String user, String name);

    boolean isInZone(@Nullable Player player, @Nullable User user);
}
