package nl.jk_5.pumpkin.server.mixin.core.server;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import nl.jk_5.pumpkin.server.mixin.interfaces.IMixinNetworkManager;

@Mixin(NetHandlerHandshakeTCP.class)
public abstract class MixinNetHandlerHandshakeTCP {

    @Shadow
    private NetworkManager networkManager;

    @Inject(method = "processHandshake", at = @At("HEAD"))
    public void onProcessHandshake(C00Handshake packetIn, CallbackInfo ci) {
        IMixinNetworkManager info = (IMixinNetworkManager) this.networkManager;
        info.setVersion(packetIn.getProtocolVersion());
        info.setVirtualHost(packetIn.ip, packetIn.port);
    }
}
