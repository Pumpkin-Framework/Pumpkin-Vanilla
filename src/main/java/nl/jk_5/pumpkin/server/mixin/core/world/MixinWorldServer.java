package nl.jk_5.pumpkin.server.mixin.core.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import nl.jk_5.pumpkin.server.multiworld.DimensionManagerImpl;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends WorldServer {

    @Inject(method = "<init>", at = @At(value = "RETURN", args = "dimensionId"))
    public void registerWorld(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn, CallbackInfo cb){
        DimensionManagerImpl.instance().setWorld(dimensionId, this);
    }

    public MixinWorldServer(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn) {
        super(server, saveHandlerIn, info, dimensionId, profilerIn);
    }
}
