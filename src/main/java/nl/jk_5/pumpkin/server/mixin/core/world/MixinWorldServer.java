package nl.jk_5.pumpkin.server.mixin.core.world;

import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import nl.jk_5.pumpkin.server.multiworld.DimensionManagerImpl;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World {

    @Inject(method = "<init>", at = @At(value = "RETURN", args = "dimensionId"))
    public void registerWorld(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn, CallbackInfo cb){
        DimensionManagerImpl.instance().setWorld(dimensionId, (WorldServer) (Object) this);
    }

    public MixinWorldServer(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }

    @Overwrite
    protected void updateWeather(){
        boolean wasRaining = this.isRaining();
        super.updateWeather();

        ServerConfigurationManager server = MinecraftServer.getServer().getConfigurationManager();

        if(this.prevRainingStrength != this.rainingStrength){
            //Raining strength changed
            server.sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.getDimensionId());
        }

        if(this.prevThunderingStrength != this.thunderingStrength){
            //Thundering strength changed
            server.sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.getDimensionId());
        }

        if(wasRaining != this.isRaining()){
            if(wasRaining){
                //Rain stopped
                server.sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(2, 0), this.provider.getDimensionId());
            }else{
                //Rain started
                server.sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(1, 0), this.provider.getDimensionId());
            }

            //Send new rain strength
            server.sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.getDimensionId());
            server.sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.getDimensionId());
        }
    }
}
