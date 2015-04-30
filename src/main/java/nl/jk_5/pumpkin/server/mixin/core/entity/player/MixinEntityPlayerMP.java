package nl.jk_5.pumpkin.server.mixin.core.entity.player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP {

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
