package nl.jk_5.pumpkin.server.mixin.core.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;
import nl.jk_5.pumpkin.server.util.interfaces.PlayerEntity;
import nl.jk_5.pumpkin.server.util.location.Location;

@Mixin(EntityPlayer.class)
@NonnullByDefault
public abstract class MixinEntityPlayer extends EntityLivingBase implements PlayerEntity {

    @Shadow
    private GameProfile gameProfile;

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Override
    public Location getLocation() {
        return new Location(Pumpkin.instance().getDimensionManager().getWorld(this.worldObj.provider.getDimensionId()), this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
    }
}
