package nl.jk_5.pumpkin.server.permission.impl.zone;

import nl.jk_5.pumpkin.server.permission.api.Area;
import nl.jk_5.pumpkin.server.permission.api.Zone;
import nl.jk_5.pumpkin.server.util.Location;

import javax.annotation.Nullable;

public class RootZone extends AbstractZone {

    private static final String name = "_ROOT_";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isInZone(Location location) {
        return false;
    }

    @Override
    public boolean isInZone(Area point) {
        return false;
    }

    @Override
    public boolean isPartOfZone(Area point) {
        return false;
    }

    @Nullable
    @Override
    public Zone getParent() {
        return null;
    }
}
