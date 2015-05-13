package nl.jk_5.pumpkin.api.command.exception;

public class InvalidUsageException extends CommandException {

    public InvalidUsageException(String message, Object... formatArgs) {
        super(message, formatArgs);
    }
}
