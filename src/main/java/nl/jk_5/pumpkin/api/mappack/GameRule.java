package nl.jk_5.pumpkin.api.mappack;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
public interface GameRule {

    String getKey();

    String getValue();
}
