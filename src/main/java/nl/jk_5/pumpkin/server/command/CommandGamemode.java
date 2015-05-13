package nl.jk_5.pumpkin.server.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.api.gamemode.GameMode;
import nl.jk_5.pumpkin.api.gamemode.GameModes;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.List;
import javax.annotation.Nullable;

@NonnullByDefault
class CommandGamemode extends BaseCommand {

    public CommandGamemode() {
        super("gamemode", "gm");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0){
            Player player = requirePlayer(sender);
            if(player.getEntity().theItemInWorldManager.getGameType().isSurvivalOrAdventure()){
                player.setGamemode(GameModes.CREATIVE);
            }else{
                player.setGamemode(GameModes.SURVIVAL);
            }
        }else if(args.length == 1){
            Player player = requirePlayer(sender);
            GameMode mode;
            if(args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s") || args[0].equals("0")){
                mode = GameModes.SURVIVAL;
            }else if(args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c") || args[0].equals("1")){
                mode = GameModes.CREATIVE;
            }else if(args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a") || args[0].equals("2")){
                mode = GameModes.ADVENTURE;
            }else if(args[0].equalsIgnoreCase("spectator") || args[0].equals("3")){
                mode = GameModes.SPECTATOR;
            }else{
                throw new CommandException("Unknown gamemode: " + args[0]);
            }
            player.setGamemode(mode);
            player.getEntity().fallDistance = 0;
        }else if(args.length == 2){
            List<Player> targets = selectPlayers(sender, args[1]);
            GameMode mode;
            if(args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s") || args[0].equals("0")){
                mode = GameModes.SURVIVAL;
            }else if(args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c") || args[0].equals("1")){
                mode = GameModes.CREATIVE;
            }else if(args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a") || args[0].equals("2")){
                mode = GameModes.ADVENTURE;
            }else if(args[0].equalsIgnoreCase("spectator") || args[0].equals("3")){
                mode = GameModes.SPECTATOR;
            }else{
                throw new CommandException("Unknown gamemode: " + args[0]);
            }

            for(Player target : targets){
                target.setGamemode(mode);
                target.getEntity().fallDistance = 0;
            }

            IChatComponent comp = new ChatComponentText("Game mode of " + targets.size() + " players changed to creative");
            comp.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(comp);
        }else{
            throw new WrongUsageException("/gamemode <mode> [player]");
        }
    }

    @Nullable
    @Override
    protected List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getOptions(args, "survival", "creative", "adventure", "spectator");
        }else if(args.length == 2){
            return getUsernameOptions(args);
        }
        return null;
    }
}
