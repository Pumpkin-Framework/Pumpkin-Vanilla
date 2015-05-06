package nl.jk_5.pumpkin.api.event.permission;

import nl.jk_5.pumpkin.api.event.Event;
import nl.jk_5.pumpkin.server.permissions.PermissionRegistrar;

public interface RegisterPermissionsEvent extends Event {

    PermissionRegistrar getRegistrar();
}
