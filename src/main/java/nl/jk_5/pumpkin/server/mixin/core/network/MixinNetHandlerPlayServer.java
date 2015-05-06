package nl.jk_5.pumpkin.server.mixin.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.event.PumpkinEventFactory;
import nl.jk_5.pumpkin.api.event.player.PlayerChatEvent;
import nl.jk_5.pumpkin.api.event.player.PlayerLeaveServerEvent;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.player.Player;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {

    @Shadow private static Logger logger;
    @Shadow private EntityPlayerMP playerEntity;
    @Shadow private int chatSpamThresholdCount;

    @Shadow public abstract void sendPacket(Packet packetIn);
    @Shadow public abstract void kickPlayerFromServer(String reason);

    @Overwrite
    public void onDisconnect(IChatComponent reason){
        MinecraftServer server = MinecraftServer.getServer();
        logger.info(this.playerEntity.getCommandSenderName() + " lost connection: " + reason);

        server.refreshStatusNextTick();

        IChatComponent leaveMessage = new ChatComponentTranslation("multiplayer.player.left", this.playerEntity.getDisplayName());
        leaveMessage.getChatStyle().setColor(EnumChatFormatting.YELLOW);

        Player player = Pumpkin.instance().getPlayerManager().getFromEntity(this.playerEntity);
        if(player == null){
            logger.warn("Player was null before being able to clean it up");
            return;
        }
        player.setOnline(false);

        PlayerLeaveServerEvent event = PumpkinEventFactory.createPlayerLeaveServerEvent(player, leaveMessage);
        Pumpkin.instance().postEvent(event);
        leaveMessage = event.getLeaveMessage();

        if(player.getMap() != null){
            Pumpkin.instance().postEvent(PumpkinEventFactory.createPlayerLeaveMapEvent(player.getMap(), player));
        }
        Pumpkin.instance().postEvent(PumpkinEventFactory.createPlayerLeaveWorldEvent(player.getWorld(), player));

        player.getWorld().onPlayerLeft(player);
        if(player.getMap() != null){
            player.getMap().onPlayerLeft(player);
        }
        player.setEntity(null);
        player.setWorld(null);
        player.setMap(null);
        player.setNetHandler(null);

        if(leaveMessage != null){
            server.getConfigurationManager().sendChatMsg(leaveMessage);
        }

        this.playerEntity.mountEntityAndWakeUp();
        server.getConfigurationManager().playerLoggedOut(this.playerEntity);
    }

    @Overwrite
    public void processChatMessage(C01PacketChatMessage packetIn){
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, (NetHandlerPlayServer) (Object) this, this.playerEntity.getServerForPlayer());

        if(this.playerEntity.getChatVisibility() == EntityPlayer.EnumChatVisibility.HIDDEN){
            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("chat.cannotSend");
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            this.sendPacket(new S02PacketChat(chatcomponenttranslation));
        }else{
            this.playerEntity.markPlayerActive();
            String msg = StringUtils.normalizeSpace(packetIn.getMessage());

            for(int i = 0; i < msg.length(); ++i){
                if(!ChatAllowedCharacters.isAllowedCharacter(msg.charAt(i))){
                    this.kickPlayerFromServer("Illegal characters in chat");
                    return;
                }
            }

            if(msg.startsWith("/")){
                MinecraftServer.getServer().getCommandManager().executeCommand(this.playerEntity, msg);
            }else{
                IChatComponent comp = new ChatComponentText("");
                IChatComponent name = this.playerEntity.getDisplayName();
                name.getChatStyle().setColor(EnumChatFormatting.GRAY);
                comp.appendSibling(name);
                name = new ChatComponentText(": ");
                name.getChatStyle().setColor(EnumChatFormatting.GRAY);
                comp.appendSibling(name);
                name = new ChatComponentText(msg);
                name.getChatStyle().setColor(EnumChatFormatting.GRAY);
                comp.appendSibling(name);

                Player player = Pumpkin.instance().getPlayerManager().getFromEntity(this.playerEntity);
                PlayerChatEvent event = PumpkinEventFactory.createPlayerChatEvent(player, msg, comp);
                Pumpkin.instance().postEvent(event);

                if(!event.isCancelled() && event.getMessage() != null){
                    MinecraftServer.getServer().getConfigurationManager().sendChatMsgImpl(event.getMessage(), false);
                }
            }

            this.chatSpamThresholdCount += 20;

            if(this.chatSpamThresholdCount > 200 && !MinecraftServer.getServer().getConfigurationManager().canSendCommands(this.playerEntity.getGameProfile())){
                this.kickPlayerFromServer("disconnect.spam");
            }
        }
    }
}
