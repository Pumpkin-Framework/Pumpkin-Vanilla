package nl.jk_5.pumpkin.api.user;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.UUID;
import javax.annotation.Nullable;

@NonnullByDefault
public interface User {

    int getId();

    String getUsername();

    String getPasswordHash();

    String getEmail();

    @Nullable
    UUID getOnlineMojangId();

    @Nullable
    UUID getOfflineMojangId();

    void setOnlineMojangId(@Nullable UUID uuid);
    void setOfflineMojangId(@Nullable UUID uuid);
}
