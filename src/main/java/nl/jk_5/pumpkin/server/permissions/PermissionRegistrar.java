package nl.jk_5.pumpkin.server.permissions;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
public interface PermissionRegistrar {

    void register(String name, boolean value);

    void register(String name, String value);
}
