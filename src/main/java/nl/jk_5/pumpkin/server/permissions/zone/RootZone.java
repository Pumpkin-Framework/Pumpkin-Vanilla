package nl.jk_5.pumpkin.server.permissions.zone;

import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.permissions.PermissionsList;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public class RootZone implements Zone {

    private PermissionsList permissionsList = new PermissionsList();

    public PermissionsList getPermissionsList() {
        return permissionsList;
    }

    @Nullable
    @Override
    public String getPermissionValue(String user, String name) {
        return permissionsList.get(name);
    }

    @Override
    public boolean isInZone(@Nullable Player player, @Nullable User user) {
        return true;
    }
}
