package nl.jk_5.pumpkin.api.mappack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldContext {

    private final String name;
    private final String subName;
    private final MappackWorld config;

    public WorldContext(@Nonnull String name, @Nonnull String subName) {
        this(name, subName, null);
    }

    public WorldContext(@Nonnull String name, @Nonnull String subName, @Nullable MappackWorld config) {
        this.name = name;
        this.subName = subName;
        this.config = config;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getSubName() {
        return subName;
    }

    @Nullable
    public MappackWorld getConfig() {
        return config;
    }
}
