package nl.jk_5.pumpkin.server.command;

import com.google.common.base.Charsets;
import org.mindrot.jbcrypt.BCrypt;

import nl.jk_5.pumpkin.api.command.CommandSender;
import nl.jk_5.pumpkin.api.command.exception.CommandException;
import nl.jk_5.pumpkin.api.command.exception.InvalidUsageException;
import nl.jk_5.pumpkin.api.text.Texts;
import nl.jk_5.pumpkin.api.text.format.TextColors;
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
    public void execute(CommandSender sender, String[] args) throws CommandException {
        Player player = requirePlayer(sender);

        if(args.length != 2){
            throw new InvalidUsageException("/login <username> <password>");
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

            player.sendMessage(Texts.of(TextColors.GREEN, "Hi, " + user.getUsername() + ". Your account is linked successfully"));
        }else{
            throw new CommandException("Wrong username/password");
        }
    }
}
