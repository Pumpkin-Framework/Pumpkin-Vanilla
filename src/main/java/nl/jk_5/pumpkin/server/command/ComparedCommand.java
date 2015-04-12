package nl.jk_5.pumpkin.server.command;

import net.minecraft.command.ICommand;

import javax.annotation.Nonnull;

abstract class ComparedCommand implements ICommand {

    @Override
    public final int compareTo(@Nonnull Object o) {
        return this.compareTo((ICommand) o);
    }

    public final int compareTo(ICommand command) {
        return this.getCommandName().compareTo(command.getCommandName());
    }
}
