package nl.jk_5.pumpkin.api.mappack;

import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
public interface MappackAuthor {

    User getUser();

    String getRole();
}
