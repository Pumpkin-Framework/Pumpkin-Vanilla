package nl.jk_5.pumpkin.api.user;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.UUID;
import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

@NonnullByDefault
public interface UserManager {

    @Nullable
    User getById(@Nonnegative int id);

    @Nullable
    User getByUsername(String username);

    @Nullable
    User getByMojangId(UUID uuid);

    void updateMojangIds(User user);
}
