package nl.jk_5.pumpkin.server.event.player.permission;

import nl.jk_5.eventbus.Event;
import nl.jk_5.pumpkin.server.permissions.PermissionRegistrar;

public class RegisterPermissionsEvent extends Event implements PermissionRegistrar {

    private final PermissionRegistrar registrar;

    public RegisterPermissionsEvent(PermissionRegistrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public void register(String name, boolean value) {
        registrar.register(name, value);
    }

    @Override
    public void register(String name, String value) {
        registrar.register(name, value);
    }
}
