package nl.jk_5.pumpkin.api.command.exception;

public class PlayerNotFoundException extends CommandException {

    public PlayerNotFoundException() {
        this("commands.generic.player.notFound");
    }

    public PlayerNotFoundException(String message, Object... formatArgs) {
        super(message, formatArgs);
    }
}
