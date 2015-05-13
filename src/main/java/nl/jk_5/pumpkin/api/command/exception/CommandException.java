package nl.jk_5.pumpkin.api.command.exception;

public class CommandException extends Exception {

    private final Object[] formatArgs;

    public CommandException(String message, Object... formatArgs) {
        super(message);
        this.formatArgs = formatArgs;
    }

    public Object[] getFormatArgs() {
        return formatArgs;
    }
}
