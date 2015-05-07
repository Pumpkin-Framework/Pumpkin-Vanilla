package nl.jk_5.pumpkin.server.util;

import com.google.common.base.Objects;

import nl.jk_5.pumpkin.api.MinecraftVersion;
import nl.jk_5.pumpkin.api.ProtocolMinecraftVersion;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
public class PumpkinMinecraftVersion implements ProtocolMinecraftVersion {

    private final String name;
    private final int protocol;

    public PumpkinMinecraftVersion(String name, int protocol) {
        this.name = name;
        this.protocol = protocol;
    }

    public static int compare(ProtocolMinecraftVersion version, MinecraftVersion to) {
        if (version.equals(to)) {
            return 0;
        } else if (to.isLegacy()) {
            return 1;
        } else {
            return version.getProtocol() - ((ProtocolMinecraftVersion) to).getProtocol();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    public boolean isLegacy() {
        return false;
    }

    @Override
    public int compareTo(MinecraftVersion o) {
        return compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProtocolMinecraftVersion)) {
            return false;
        }

        ProtocolMinecraftVersion that = (ProtocolMinecraftVersion) o;
        return this.getProtocol() == that.getProtocol();

    }

    @Override
    public int hashCode() {
        return this.protocol;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", this.name)
                .add("protocol", this.protocol)
                .toString();
    }
}
