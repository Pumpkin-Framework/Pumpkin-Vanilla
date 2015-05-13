package nl.jk_5.pumpkin.server.mixin.core.command;

import net.minecraft.command.ICommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.command.CommandSender;
import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@Mixin(targets = "net/minecraft/command/CommandExecuteAt$1")
@NonnullByDefault
public abstract class MixinCommandExecuteAt implements CommandSender, ICommandSender {

    @Shadow(aliases = "val$sender")
    private ICommandSender field_174802_b;

    @Override
    public void sendMessage(Text... messages) {
        ((CommandSender) field_174802_b).sendMessage(messages);
    }

    @Override
    public void sendMessage(Iterable<Text> messages) {
        ((CommandSender) field_174802_b).sendMessage(messages);
    }

    @Override
    public String getName() {
        return ((CommandSender) field_174802_b).getName();
    }
}
