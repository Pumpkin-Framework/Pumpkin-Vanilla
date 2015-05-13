package nl.jk_5.pumpkin.api.command.exception;

public class InvalidSyntaxException extends CommandException {

    public InvalidSyntaxException(){
        this("commands.generic.syntax");
    }

    public InvalidSyntaxException(String message, Object... formatArgs) {
        super(message, formatArgs);
    }
}
