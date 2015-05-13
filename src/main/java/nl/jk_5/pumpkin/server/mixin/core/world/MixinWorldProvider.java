package nl.jk_5.pumpkin.server.mixin.core.world;

import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mixin.interfaces.IMixinWorldProvider;
import nl.jk_5.pumpkin.server.multiworld.DelegatingWorldProvider;
import nl.jk_5.pumpkin.server.multiworld.DimensionManagerImpl;

@Mixin(WorldProvider.class)
public abstract class MixinWorldProvider implements IMixinWorldProvider {

    @Shadow
    private int dimensionId;

    public void setDimensionId(int dimid){
        this.dimensionId = dimid;
        if(((Object) this) instanceof DelegatingWorldProvider){
            ((DelegatingWorldProvider) (Object) this).setWrappedDimension(dimid);
        }
    }

    @Overwrite
    public static WorldProvider getProviderForDimension(int dimensionId){
        return ((DimensionManagerImpl) Pumpkin.instance().getDimensionManager()).createProviderFor(dimensionId);
    }
}
