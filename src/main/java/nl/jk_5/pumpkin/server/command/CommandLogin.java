package nl.jk_5.pumpkin.server.command;

import com.google.common.base.Charsets;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.mindrot.jbcrypt.BCrypt;

import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.UUID;

@NonnullByDefault
class CommandLogin extends BaseCommand {

    public CommandLogin() {
        super("login");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        Player player = requirePlayer(sender);

        if(args.length != 2){
            throw new WrongUsageException("/login <username> <password>");
        }
        String username = args[0];
        String password = args[1];

        User user = Pumpkin.instance().getUserManager().getByUsername(username);
        if(user == null){
            throw new CommandException("Wrong username/password");
        }
        if(BCrypt.checkpw(password, user.getPasswordHash())){
            UUID offlineId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes(Charsets.UTF_8));
            boolean offline = offlineId.equals(player.getUuid());

            if(offline){
                user.setOfflineMojangId(offlineId);
            }else{
                user.setOnlineMojangId(player.getUuid());
            }

            Pumpkin.instance().getUserManager().updateMojangIds(user);

            IChatComponent comp = new ChatComponentText("Hi, " + user.getUsername() + ". Your account is linked successfully");
            comp.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(comp);
        }else{
            throw new CommandException("Wrong username/password");
        }
    }
}
