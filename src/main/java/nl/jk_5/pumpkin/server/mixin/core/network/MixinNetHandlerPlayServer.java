package nl.jk_5.pumpkin.server.mixin.core.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.event.player.PlayerLeaveServerEvent;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer {

    @Shadow private static Logger logger;
    @Shadow private EntityPlayerMP playerEntity;

    @Overwrite
    public void onDisconnect(IChatComponent reason){
        MinecraftServer server = MinecraftServer.getServer();
        logger.info(this.playerEntity.getCommandSenderName() + " lost connection: " + reason);

        server.refreshStatusNextTick();

        IChatComponent leaveMessage = new ChatComponentTranslation("multiplayer.player.left", this.playerEntity.getDisplayName());
        leaveMessage.getChatStyle().setColor(EnumChatFormatting.YELLOW);

        PlayerLeaveServerEvent event = Pumpkin.instance().postEvent(new PlayerLeaveServerEvent(this.playerEntity, leaveMessage));
        leaveMessage = event.getLeaveMessage();

        if(leaveMessage != null){
            server.getConfigurationManager().sendChatMsg(leaveMessage);
        }

        this.playerEntity.mountEntityAndWakeUp();
        server.getConfigurationManager().playerLoggedOut(this.playerEntity);
    }
}
