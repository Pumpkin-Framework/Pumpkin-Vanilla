package nl.jk_5.pumpkin.server.mixin.core;

import net.minecraft.init.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Bootstrap.class)
public class MixinBootstrap {

    @Overwrite
    private static void redirectOutputToLog() {
        // We do that on our own
    }
}
