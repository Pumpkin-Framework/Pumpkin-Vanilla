package nl.jk_5.pumpkin.server.mixin.core.entity;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

import nl.jk_5.pumpkin.api.command.CommandSender;
import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.server.text.PumpkinTexts;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
@Mixin(Entity.class)
public abstract class MixinEntity implements CommandSender, ICommandSender {

    @Override
    public void sendMessage(Text... messages) {
        for (Text message : messages) {
            addChatMessage(PumpkinTexts.toComponent(message));
        }
    }

    @Override
    public void sendMessage(Iterable<Text> messages) {
        for (Text message : messages) {
            addChatMessage(PumpkinTexts.toComponent(message));
        }
    }

    @Override
    public String getName() {
        return getCommandSenderName();
    }
}
