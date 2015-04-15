package nl.jk_5.pumpkin.server.command;

import net.minecraft.command.*;
import net.minecraft.command.common.CommandReplaceItem;
import net.minecraft.command.server.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("UnusedDeclaration")
public class PumpkinCommandManager extends CommandHandler implements IAdminCommand {

    //This method is used in MinecraftServerTransformer
    @SuppressWarnings("UnusedDeclaration")
    public static PumpkinCommandManager create(){
        return new PumpkinCommandManager();
    }

    public PumpkinCommandManager() {

        //Pumpkin Commands
        this.registerCommand(new CommandMap());
        this.registerCommand(new CommandGoto());

        //Vanilla commands commands
        this.registerCommand(new CommandTime());
        this.registerCommand(new CommandGameMode());
        this.registerCommand(new CommandDifficulty());
        this.registerCommand(new CommandDefaultGameMode());
        this.registerCommand(new CommandKill());
        this.registerCommand(new CommandToggleDownfall());
        this.registerCommand(new CommandWeather());
        this.registerCommand(new CommandXP());
        this.registerCommand(new CommandTeleport());
        this.registerCommand(new CommandGive());
        this.registerCommand(new CommandReplaceItem());
        this.registerCommand(new CommandStats());
        this.registerCommand(new CommandEffect());
        this.registerCommand(new CommandEnchant());
        this.registerCommand(new CommandParticle());
        this.registerCommand(new CommandEmote());
        this.registerCommand(new CommandShowSeed());
        this.registerCommand(new CommandHelp());
        this.registerCommand(new CommandDebug());
        this.registerCommand(new CommandMessage());
        this.registerCommand(new CommandBroadcast());
        this.registerCommand(new CommandSetSpawnpoint());
        this.registerCommand(new CommandSetDefaultSpawnpoint());
        this.registerCommand(new CommandGameRule());
        this.registerCommand(new CommandClearInventory());
        this.registerCommand(new CommandTestFor());
        this.registerCommand(new CommandSpreadPlayers());
        this.registerCommand(new CommandPlaySound());
        this.registerCommand(new CommandScoreboard());
        this.registerCommand(new CommandExecuteAt());
        this.registerCommand(new CommandTrigger());
        this.registerCommand(new CommandAchievement());
        this.registerCommand(new CommandSummon());
        this.registerCommand(new CommandSetBlock());
        this.registerCommand(new CommandFill());
        this.registerCommand(new CommandClone());
        this.registerCommand(new CommandCompare());
        this.registerCommand(new CommandBlockData());
        this.registerCommand(new CommandTestForBlock());
        this.registerCommand(new CommandMessageRaw());
        this.registerCommand(new CommandWorldBorder());
        this.registerCommand(new CommandTitle());
        this.registerCommand(new CommandEntityData());

        //Server-only vanilla commands
        this.registerCommand(new CommandOp());
        this.registerCommand(new CommandDeOp());
        this.registerCommand(new CommandStop());
        this.registerCommand(new CommandSaveAll());
        this.registerCommand(new CommandSaveOff());
        this.registerCommand(new CommandSaveOn());
        this.registerCommand(new CommandBanIp());
        this.registerCommand(new CommandPardonIp());
        this.registerCommand(new CommandBanPlayer());
        this.registerCommand(new CommandListBans());
        this.registerCommand(new CommandPardonPlayer());
        this.registerCommand(new CommandServerKick());
        this.registerCommand(new CommandListPlayers());
        this.registerCommand(new CommandWhitelist());
        this.registerCommand(new CommandSetPlayerTimeout());

        CommandBase.setAdminCommander(this);
    }

    public void notifyOperators(ICommandSender sender, ICommand command, int p_152372_3_, String msgFormat, Object ... msgParams){
        boolean sendFeedback = true;
        MinecraftServer server = MinecraftServer.getServer();

        if(!sender.sendCommandFeedback()){
            sendFeedback = false;
        }

        ChatComponentTranslation message = new ChatComponentTranslation("chat.type.admin", sender.getCommandSenderName(), new ChatComponentTranslation(msgFormat, msgParams));
        message.getChatStyle().setColor(EnumChatFormatting.GRAY);
        message.getChatStyle().setItalic(true);

        if(sendFeedback){
            for(Object playerObj : server.getConfigurationManager().playerEntityList){
                EntityPlayer player = (EntityPlayer) playerObj;

                if(player != sender && server.getConfigurationManager().canSendCommands(player.getGameProfile()) && command.canCommandSenderUseCommand(sender)){
                    player.addChatMessage(message);
                }
            }
        }

        if(sender != server && sender.getEntityWorld().getGameRules().getGameRuleBooleanValue("logAdminCommands")){
            server.addChatMessage(message);
        }

        boolean flag1 = sender.getEntityWorld().getGameRules().getGameRuleBooleanValue("sendCommandFeedback");

        if(sender instanceof CommandBlockLogic){
            flag1 = ((CommandBlockLogic) sender).shouldTrackOutput();
        }

        if((p_152372_3_ & 1) != 1 && flag1){
            sender.addChatMessage(new ChatComponentTranslation(msgFormat, msgParams));
        }
    }
}
