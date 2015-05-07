package nl.jk_5.pumpkin.server.mixin.core.server;

import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.MinecraftVersion;
import nl.jk_5.pumpkin.server.mixin.interfaces.IMixinNetworkManager;
import nl.jk_5.pumpkin.server.util.PumpkinMinecraftVersion;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager extends SimpleChannelInboundHandler implements IMixinNetworkManager {

    @Shadow
    public abstract SocketAddress getRemoteAddress();

    private InetSocketAddress virtualHost;
    private MinecraftVersion version;

    @Override
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) getRemoteAddress();
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        return this.virtualHost;
    }

    @Override
    public void setVirtualHost(String host, int port) {
        this.virtualHost = InetSocketAddress.createUnresolved(host, port);
    }

    @Override
    public MinecraftVersion getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(int version) {
        this.version = new PumpkinMinecraftVersion(String.valueOf(version), version);
    }
}
