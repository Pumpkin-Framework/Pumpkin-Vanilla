package nl.jk_5.pumpkin.api.command.exception;

public class EntityNotFoundException extends CommandException {

    public EntityNotFoundException() {
        this("commands.generic.entity.notFound");
    }

    public EntityNotFoundException(String message, Object... formatArgs) {
        super(message, formatArgs);
    }
}
