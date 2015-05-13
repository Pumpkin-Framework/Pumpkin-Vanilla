package nl.jk_5.pumpkin.server.status;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import net.minecraft.network.NetworkManager;

import nl.jk_5.pumpkin.api.MinecraftVersion;
import nl.jk_5.pumpkin.api.status.StatusClient;
import nl.jk_5.pumpkin.server.mixin.interfaces.IMixinNetworkManager;

import java.net.InetSocketAddress;

public class PumpkinStatusClient implements StatusClient {

    private final IMixinNetworkManager connection;

    public PumpkinStatusClient(NetworkManager networkManager) {
        this.connection = (IMixinNetworkManager) networkManager;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.connection.getAddress();
    }

    @Override
    public MinecraftVersion getVersion() {
        return this.connection.getVersion();
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
        return Optional.fromNullable(this.connection.getVirtualHost());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PumpkinStatusClient)) {
            return false;
        }

        PumpkinStatusClient that = (PumpkinStatusClient) o;
        return Objects.equal(this.connection, that.connection);

    }

    @Override
    public int hashCode() {
        return this.connection.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.connection)
                .toString();
    }
}