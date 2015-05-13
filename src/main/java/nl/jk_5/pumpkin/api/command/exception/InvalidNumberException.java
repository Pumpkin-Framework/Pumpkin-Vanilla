package nl.jk_5.pumpkin.api.command.exception;

public class InvalidNumberException extends CommandException {

    public InvalidNumberException() {
        this("commands.generic.num.invalid");
    }

    public InvalidNumberException(String message, Object... formatArgs) {
        super(message, formatArgs);
    }
}
