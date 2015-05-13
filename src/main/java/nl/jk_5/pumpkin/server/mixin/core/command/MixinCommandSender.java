package nl.jk_5.pumpkin.server.mixin.core.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

import nl.jk_5.pumpkin.api.command.CommandSender;
import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.server.text.PumpkinTexts;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
@Mixin({EntityPlayerMP.class, CommandBlockLogic.class, MinecraftServer.class, RConConsoleSource.class})
public abstract class MixinCommandSender implements ICommandSender, CommandSender {

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
