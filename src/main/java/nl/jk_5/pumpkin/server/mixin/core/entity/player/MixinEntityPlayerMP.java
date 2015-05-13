package nl.jk_5.pumpkin.server.mixin.core.entity.player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends MixinEntityPlayer {

    public MixinEntityPlayerMP(World world) {
        super(world);
    }

    @Overwrite
    public IChatComponent getTabListDisplayName(){
        //TODO
        return null; //new ChatComponentText("Lol i am way longer than 16 chars");
    }

    @Overwrite
    public boolean canCommandSenderUseCommand(int permLevel, String commandName){
        return true;
    }
}
