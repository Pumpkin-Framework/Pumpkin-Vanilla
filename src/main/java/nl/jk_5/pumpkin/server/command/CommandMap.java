package nl.jk_5.pumpkin.server.command;

import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import nl.jk_5.pumpkin.api.mappack.Mappack;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
public class CommandMap extends BaseCommand {

    public CommandMap() {
        super("map");
    }

    @Override
    public void execute(final ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) throw new WrongUsageException("/map <create:remove:list>");
        if(args[0].equalsIgnoreCase("create")){
            if(args.length == 1) throw new WrongUsageException("/map create <mappack-id>");
            int id = CommandBase.parseInt(args[1]);
            Mappack mappack = Pumpkin.instance().getMappackRegistry().getById(id);

            if(mappack != null){
                ChatComponentText component = new ChatComponentText("Loading " + mappack.getName());
                component.getChatStyle().setColor(EnumChatFormatting.GREEN);
                sender.addChatMessage(component);

                final ListenableFuture<Map> future = Pumpkin.instance().getMapLoader().createMap(mappack);
                future.addListener(new Runnable() {
                    @Override
                    public void run() {
                        Map map = getFutureResult(future);
                        ChatComponentText component = new ChatComponentText("Loaded ");
                        component.getChatStyle().setColor(EnumChatFormatting.GREEN);

                        ChatComponentText comp = new ChatComponentText(map.getInternalName());
                        comp.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Teleport to the map")));
                        comp.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goto " + map.getInternalName()));
                        component.appendSibling(comp);
                        sender.addChatMessage(component);
                    }
                }, Pumpkin.instance().getExecutor());
            }else{
                throw new CommandException("Mappack does not exist");
            }
        }
    }
}
