package nl.jk_5.pumpkin.server.status;

import nl.jk_5.pumpkin.api.MinecraftVersion;

public class PumpkinLegacyMinecraftVersion implements MinecraftVersion {

    public static final PumpkinLegacyMinecraftVersion V1_3 = new PumpkinLegacyMinecraftVersion("<=1.3", 39);
    public static final PumpkinLegacyMinecraftVersion V1_5 = new PumpkinLegacyMinecraftVersion("1.4-1.5", 61);
    public static final PumpkinLegacyMinecraftVersion V1_6 = new PumpkinLegacyMinecraftVersion("1.6", 78);

    private final String name;
    private final int latestVersion;

    private PumpkinLegacyMinecraftVersion(String name, int latestVersion) {
        this.name = name;
        this.latestVersion = latestVersion;
    }

    public PumpkinLegacyMinecraftVersion(PumpkinLegacyMinecraftVersion base, int version) {
        this.name = base.name;
        this.latestVersion = version;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isLegacy() {
        return true;
    }

    @Override
    public int compareTo(MinecraftVersion o) {
        if (o == this) {
            return 0;
        } else if (!o.isLegacy()) {
            return -1;
        } else {
            return this.latestVersion - ((PumpkinLegacyMinecraftVersion) o).latestVersion;
        }
    }
}
