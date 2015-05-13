package nl.jk_5.pumpkin.server.command;

import com.google.common.util.concurrent.ListenableFuture;

import nl.jk_5.pumpkin.api.command.CommandSender;
import nl.jk_5.pumpkin.api.command.exception.CommandException;
import nl.jk_5.pumpkin.api.command.exception.InvalidUsageException;
import nl.jk_5.pumpkin.api.mappack.Mappack;
import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.Texts;
import nl.jk_5.pumpkin.api.text.action.TextActions;
import nl.jk_5.pumpkin.api.text.format.TextColors;
import nl.jk_5.pumpkin.api.text.format.TextStyles;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
class CommandMap extends BaseCommand {

    public CommandMap() {
        super("map");
    }

    @Override
    public void execute(final CommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) throw new InvalidUsageException("/map <create:remove:list>");
        if(args[0].equalsIgnoreCase("create")){
            if(args.length == 1) throw new InvalidUsageException("/map create <mappack-id>");
            int id = parseInt(args[1]);
            Mappack mappack = Pumpkin.instance().getMappackRegistry().getById(id);

            if(mappack != null){
                Text msg = Texts.of(TextColors.GREEN, "Loading " + mappack.getName());
                sender.sendMessage(msg);

                final ListenableFuture<Map> future = Pumpkin.instance().getMapLoader().createMap(mappack);
                future.addListener(new Runnable() {
                    @Override
                    public void run() {
                        Map map = getFutureResult(future);
                        if(map == null) return;

                        Text namePart = Texts.of(TextColors.GRAY, TextStyles.UNDERLINE, map.getInternalName()).builder()
                                .onClick(TextActions.runCommand("/goto " + map.getInternalName()))
                                .onHover(TextActions.showText(Texts.of("Teleport to the map"))).build();
                        Text msg = Texts.of(TextColors.GREEN, "Loaded ", namePart);

                        sender.sendMessage(msg);
                    }
                }, Pumpkin.instance().getExecutor());
            }else{
                throw new CommandException("Mappack does not exist");
            }
        }
    }
}
