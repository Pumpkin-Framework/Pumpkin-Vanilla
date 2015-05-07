package nl.jk_5.pumpkin.server.mixin.interfaces;

import nl.jk_5.pumpkin.api.MinecraftVersion;

import java.net.InetSocketAddress;

public interface IMixinNetworkManager {

    InetSocketAddress getAddress();

    InetSocketAddress getVirtualHost();

    void setVirtualHost(String host, int port);

    MinecraftVersion getVersion();

    void setVersion(int version);
}
