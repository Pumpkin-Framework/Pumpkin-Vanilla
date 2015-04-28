package nl.jk_5.pumpkin.server.command;

import static net.minecraft.command.CommandResultStats.Type.AFFECTED_ENTITIES;
import static net.minecraft.command.CommandResultStats.Type.SUCCESS_COUNT;

import net.minecraft.command.*;
import net.minecraft.command.common.CommandReplaceItem;
import net.minecraft.command.server.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.permissions.PermissionCommand;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.CollectionUtils;

import java.util.List;

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
        this.registerCommand(new CommandLogin());

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

    public void notifyOperators(ICommandSender sender, ICommand command, int flags, String msgFormat, Object ... msgParams){
        MinecraftServer server = MinecraftServer.getServer();

        ChatComponentTranslation message = new ChatComponentTranslation("chat.type.admin", sender.getCommandSenderName(), new ChatComponentTranslation(msgFormat, msgParams));
        message.getChatStyle().setColor(EnumChatFormatting.GRAY);
        message.getChatStyle().setItalic(true);

        if(sender.sendCommandFeedback()){
            for(EntityPlayer player : CollectionUtils.toType(server.getConfigurationManager().playerEntityList, EntityPlayer.class)){
                if(player != sender && server.getConfigurationManager().canSendCommands(player.getGameProfile()) && command.canCommandSenderUseCommand(sender)){
                    player.addChatMessage(message);
                }
            }
        }

        if(sender != server && sender.getEntityWorld().getGameRules().getGameRuleBooleanValue("logAdminCommands")){
            server.addChatMessage(message);
        }

        boolean sendFeedback = sender.getEntityWorld().getGameRules().getGameRuleBooleanValue("sendCommandFeedback");

        if(sender instanceof CommandBlockLogic){
            sendFeedback = ((CommandBlockLogic) sender).shouldTrackOutput();
        }

        if((flags & 1) != 1 && sendFeedback){
            sender.addChatMessage(new ChatComponentTranslation(msgFormat, msgParams));
        }
    }

    @Override
    public int executeCommand(ICommandSender sender, String input) {
        input = input.trim();

        if(input.startsWith("/")){
            input = input.substring(1);
        }

        String[] args = input.split(" ");
        String commandName = args[0];
        args = dropFirstString(args);
        ICommand command = (ICommand) this.getCommands().get(commandName);
        int usernameIndex = getUsernameIndex(command, args);
        int success = 0;

        if(command == null){
            IChatComponent comp = new ChatComponentTranslation("commands.generic.notFound");
            comp.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(comp);
            return 0;
        }

        boolean hasPermission = sender instanceof CommandBlockLogic || sender instanceof MinecraftServer || sender instanceof RConConsoleSource;

        if(sender instanceof EntityPlayerMP){
            Player player = Pumpkin.instance().getPlayerManager().getFromEntity(((EntityPlayerMP) sender));
            String permName = "mc.command." + command.getCommandName();
            if(command instanceof PermissionCommand){
                permName = ((PermissionCommand) command).getPermission();
            }
            hasPermission = Pumpkin.instance().getPermissionsHandler().hasPermission(player, permName);
        }

        if(!hasPermission){
            IChatComponent comp = new ChatComponentTranslation("commands.generic.permission");
            comp.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(comp);
            return 0;
        }

        if(usernameIndex > -1){
            //TODO: add an expandable selector
            List matched = PlayerSelector.matchEntities(sender, args[usernameIndex], Entity.class);
            String usernameInput = args[usernameIndex];
            sender.setCommandStat(AFFECTED_ENTITIES, matched.size());

            for(Entity entity : CollectionUtils.toType(matched, Entity.class)){
                args[usernameIndex] = entity.getUniqueID().toString();
                if(this.tryExecute(sender, args, command, input)){
                    success ++;
                }
            }

            args[usernameIndex] = usernameInput;
        }else{
            sender.setCommandStat(AFFECTED_ENTITIES, 1);

            if(this.tryExecute(sender, args, command, input)){
                success++;
            }
        }

        sender.setCommandStat(SUCCESS_COUNT, success);
        return success;
    }

    @Override
    public ICommand registerCommand(ICommand command) {
        String permName = "mc.command." + command.getCommandName();
        if(command instanceof PermissionCommand){
            permName = ((PermissionCommand) command).getPermission();
        }
        String defaultValue = "false";
        if(command instanceof CommandBase){
            defaultValue = ((CommandBase) command).getRequiredPermissionLevel() == 0 ? "true" : "false";
        }

        Pumpkin.instance().getPermissionsHandler().register(permName, defaultValue);

        return super.registerCommand(command);
    }

    private static String[] dropFirstString(String[] input){
        String[] newArray = new String[input.length - 1];
        System.arraycopy(input, 1, newArray, 0, input.length - 1);
        return newArray;
    }

    private static int getUsernameIndex(ICommand command, String[] args){
        if(command == null){
            return -1;
        }
        for(int i = 0; i < args.length; ++i){
            if(command.isUsernameIndex(args, i) && PlayerSelector.matchesMultiplePlayers(args[i])){
                return i;
            }
        }
        return -1;
    }
}
