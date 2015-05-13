package nl.jk_5.pumpkin.server.status;

import com.google.common.base.Optional;

import nl.jk_5.pumpkin.api.MinecraftVersion;
import nl.jk_5.pumpkin.api.status.StatusClient;

import java.net.InetSocketAddress;

public class PumpkinLegacyStatusClient implements StatusClient {

    private final InetSocketAddress address;
    private final MinecraftVersion version;
    private final Optional<InetSocketAddress> virtualHost;

    public PumpkinLegacyStatusClient(InetSocketAddress address, MinecraftVersion version, InetSocketAddress virtualHost) {
        this.address = address;
        if (version != null) {
            this.version = version;
        } else {
            this.version = version;
        }
        this.virtualHost = Optional.fromNullable(virtualHost);
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    @Override
    public MinecraftVersion getVersion() {
        return this.version;
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
        return this.virtualHost;
    }
}
