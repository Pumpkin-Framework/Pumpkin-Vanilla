package nl.jk_5.pumpkin.api.mappack;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public interface MappackRegistry {

    @Nullable
    Mappack getById(int id);
}
