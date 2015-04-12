package nl.jk_5.pumpkin.server.mixin.core.world.chunk.storage;

import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatOld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.File;

@Mixin(AnvilSaveConverter.class)
public abstract class MixinAnvilSaveConverter extends SaveFormatOld {

    public MixinAnvilSaveConverter(File worldDir) {
        super(worldDir);
    }

    @Overwrite
    public ISaveHandler getSaveLoader(String name, boolean p_75804_2_){
        return new AnvilSaveHandler(this.savesDirectory, name, false);
    }
}
