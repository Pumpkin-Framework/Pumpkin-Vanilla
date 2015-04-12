package nl.jk_5.pumpkin.server.mixin.core.server.dedicated;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import nl.jk_5.pumpkin.server.Pumpkin;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {

    @Inject(method = "startServer", at = @At(value = "INVOKE_STRING", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", args = {"ldc=Loading properties"}, shift = At.Shift.BY, by = -2, remap = false))
    public void onServerLoad(CallbackInfoReturnable<Boolean> ci) {
        Pumpkin.instance().load();
    }

    @Inject(method = "startServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;setConfigManager(Lnet/minecraft/server/management/ServerConfigurationManager;)V", shift = At.Shift.BY, by = -7))
    public void onServerInitialize(CallbackInfoReturnable<Boolean> ci) {
        Pumpkin.instance().initialize();
    }


}
