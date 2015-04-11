package nl.jk_5.pumpkin.server.mixin.core.world.chunk.storage;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.SaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.File;

@Mixin(AnvilSaveHandler.class)
public abstract class MixinAnvilSaveHandler extends SaveHandler {

    public MixinAnvilSaveHandler(File savesDirectory, String directoryName, boolean playersDirectoryIn) {
        super(savesDirectory, directoryName, playersDirectoryIn);
    }

    @Overwrite
    public IChunkLoader getChunkLoader(WorldProvider provider) {
        return new AnvilChunkLoader(this.getWorldDirectory());
    }
}
