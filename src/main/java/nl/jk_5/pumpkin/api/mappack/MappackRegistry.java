package nl.jk_5.pumpkin.api.mappack;

import javax.annotation.Nullable;

public interface MappackRegistry {

    @Nullable
    Mappack getById(int id);
}
