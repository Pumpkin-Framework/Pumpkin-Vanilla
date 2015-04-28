package nl.jk_5.pumpkin.server.permissions;

import javax.annotation.Nonnull;

public interface PermissionCommand {

    @Nonnull
    String getPermission();

    boolean defaultPermission();
}
