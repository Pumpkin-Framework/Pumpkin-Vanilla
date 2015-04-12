package nl.jk_5.pumpkin.server.mixin.core.world.storage;

import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.SaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import nl.jk_5.pumpkin.server.storage.PlayerNbtManager;

@Mixin(SaveHandler.class)
public class MixinSaveHandler {

    @Overwrite
    public IPlayerFileData getPlayerNBTManager(){
        return PlayerNbtManager.instance();
    }
}
