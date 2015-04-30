package nl.jk_5.pumpkin.server.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.multiworld.teleport.TeleportOptions;
import nl.jk_5.pumpkin.server.multiworld.teleport.Teleporter;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
class CommandGoto extends BaseCommand {

    public CommandGoto() {
        super("goto");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) throw new WrongUsageException("/goto <world-id>");
        Player player = requirePlayer(sender);
        MapWorld target = Pumpkin.instance().getDimensionManager().getWorld(CommandBase.parseInt(args[0]));
        if(target == null){
            throw new CommandException("That world does not exist");
        }
        Teleporter.teleportPlayer(player, new TeleportOptions(target.getConfig().getSpawnpoint().setWorld(target)));
    }
}
