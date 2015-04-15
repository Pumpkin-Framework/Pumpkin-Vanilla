package nl.jk_5.pumpkin.api.mappack;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
public class WorldContext {

    private final String name;
    private final String subName;
    private final MappackWorld config;

    public WorldContext(String name, String subName, MappackWorld config) {
        this.name = name;
        this.subName = subName;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public String getSubName() {
        return subName;
    }

    public MappackWorld getConfig() {
        return config;
    }
}
