package nl.jk_5.pumpkin.server.command;

import nl.jk_5.pumpkin.api.command.CommandSender;
import nl.jk_5.pumpkin.api.command.exception.CommandException;
import nl.jk_5.pumpkin.api.command.exception.InvalidUsageException;
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
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) throw new InvalidUsageException("/goto <world-id>");
        Player player = requirePlayer(sender);
        MapWorld target = Pumpkin.instance().getDimensionManager().getWorld(parseInt(args[0]));
        if(target == null){
            throw new CommandException("That world does not exist");
        }
        Teleporter.teleportPlayer(player, new TeleportOptions(target.getConfig().getSpawnpoint().setWorld(target)));
    }
}
