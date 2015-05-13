package nl.jk_5.pumpkin.server.mixin.core.status;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerStatusServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.server.status.PumpkinStatusClient;
import nl.jk_5.pumpkin.server.status.PumpkinStatusResponse;

@Mixin(NetHandlerStatusServer.class)
public class MixinNetHandlerStatusServer {

    @Shadow
    private MinecraftServer server;

    @Shadow
    private NetworkManager networkManager;

    @Overwrite
    public void processServerQuery(C00PacketServerQuery packetIn) {
        ServerStatusResponse response = PumpkinStatusResponse.post(this.server, new PumpkinStatusClient(this.networkManager));
        if(response != null){
            this.networkManager.sendPacket(new S00PacketServerInfo(response));
        }else{
            this.networkManager.closeChannel(null);
        }
    }
}
