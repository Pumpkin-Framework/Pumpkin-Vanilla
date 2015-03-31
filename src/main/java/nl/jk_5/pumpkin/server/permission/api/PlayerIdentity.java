package nl.jk_5.pumpkin.server.permission.api;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.UUID;

@NonnullByDefault
public interface PlayerIdentity {

    UUID getId();

    String getUsername();
}
