package nl.jk_5.pumpkin.server.mixin.core.server;

import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.*;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import nl.jk_5.pumpkin.api.event.PumpkinEventFactory;
import nl.jk_5.pumpkin.api.event.player.PlayerChatEvent;
import nl.jk_5.pumpkin.api.event.player.PlayerLeaveServerEvent;
import nl.jk_5.pumpkin.api.net.PlayerConnection;
import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.Texts;
import nl.jk_5.pumpkin.api.text.action.TextActions;
import nl.jk_5.pumpkin.api.text.format.TextColors;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mixin.interfaces.IMixinNetworkManager;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.text.PumpkinTexts;

import java.net.InetSocketAddress;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer implements PlayerConnection {

    @Shadow private static Logger logger;
    @Shadow private EntityPlayerMP playerEntity;
    @Shadow private int chatSpamThresholdCount;
    @Shadow public NetworkManager netManager;

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
                //TODO: name team color
                Text name = Texts.of(playerEntity.getCommandSenderName()).builder()
                        .onClick(TextActions.suggestCommand("/msg " + this.playerEntity.getCommandSenderName() + " "))
                        .onHover(TextActions.showEntity(this.playerEntity, this.playerEntity.getCommandSenderName()))
                        .onShiftClick(TextActions.insertText(this.playerEntity.getCommandSenderName()))
                        .build();
                Text text = Texts.of(TextColors.GRAY, name, ": ", msg);

                Player player = Pumpkin.instance().getPlayerManager().getFromEntity(this.playerEntity);
                PlayerChatEvent event = PumpkinEventFactory.createPlayerChatEvent(player, msg, text);
                Pumpkin.instance().postEvent(event);

                if(!event.isCancelled() && event.getMessage() != null){
                    MinecraftServer.getServer().getConfigurationManager().sendChatMsgImpl(PumpkinTexts.toComponent(text), false);
                }
            }

            this.chatSpamThresholdCount += 20;

            if(this.chatSpamThresholdCount > 200 && !MinecraftServer.getServer().getConfigurationManager().canSendCommands(this.playerEntity.getGameProfile())){
                this.kickPlayerFromServer("disconnect.spam");
            }
        }
    }

    @Override
    public Player getPlayer() {
        return Pumpkin.instance().getPlayerManager().getFromEntity(this.playerEntity);
    }

    @Override
    public InetSocketAddress getAddress() {
        return ((IMixinNetworkManager) this.netManager).getAddress();
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        return ((IMixinNetworkManager) this.netManager).getVirtualHost();
    }

    @Override
    public int getPing() {
        return this.playerEntity.ping;
    }

    /**
     * @author jk-5
     *
     * Purpose: replace the logic used for command blocks to make functional
     *
     * @param ci callback
     * @param packetIn method param
     */
    @Inject(method = "processVanilla250Packet", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "net/minecraft/network/PacketThreadUtil.checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"), cancellable = true)
    public void processCommandBlock(C17PacketCustomPayload packetIn, CallbackInfo ci) {
        if ("MC|AdvCdm".equals(packetIn.getChannelName())) {
            try {
                PacketBuffer buffer = packetIn.getBufferData();

                try{
                    byte type = buffer.readByte();
                    CommandBlockLogic logic = null;

                    String permissionCheck = null;

                    switch(type){
                        case 0:
                            TileEntity tileEntity = this.playerEntity.worldObj.getTileEntity(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
                            if(tileEntity instanceof TileEntityCommandBlock){
                                logic = ((TileEntityCommandBlock) tileEntity).getCommandBlockLogic();
                                permissionCheck = "minecraft.commandblock.edit.block." + logic.getCommandSenderName();
                            }
                            break;
                        case 1:
                            Entity entity = this.playerEntity.worldObj.getEntityByID(buffer.readInt());

                            if(entity instanceof EntityMinecartCommandBlock){
                                logic = ((EntityMinecartCommandBlock) entity).getCommandBlockLogic();
                                permissionCheck = "minecraft.commandblock.edit.minecart." + logic.getCommandSenderName();
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown command block type!");
                    }

                    Player player = Pumpkin.instance().getPlayerManager().getFromEntity(this.playerEntity);
                    if(permissionCheck == null || !player.hasPermission(permissionCheck)){
                        IChatComponent comp = new ChatComponentText("You do not have permission to edit this command block!");
                        comp.getChatStyle().setColor(EnumChatFormatting.RED);
                        player.getEntity().addChatMessage(comp);
                        return;
                    }

                    String newCommand = buffer.readStringFromBuffer(buffer.readableBytes());
                    boolean trackOutput = buffer.readBoolean();

                    if(logic != null){
                        logic.setCommand(newCommand);
                        logic.setTrackOutput(trackOutput);

                        if(!trackOutput){
                            logic.setLastOutput(null);
                        }

                        logic.updateCommand();
                        this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", newCommand));
                    }
                }catch(Exception e){
                    logger.error("Couldn\'t set command block", e);
                }finally{
                    buffer.release();
                }
            }finally{
                ci.cancel();
            }
        }

    }
}
