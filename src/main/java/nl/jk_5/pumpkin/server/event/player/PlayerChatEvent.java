package nl.jk_5.pumpkin.server.event.player;

import net.minecraft.util.IChatComponent;

import nl.jk_5.eventbus.Event;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
@Event.Cancelable
public class PlayerChatEvent extends PlayerEvent {

    private final String originalMessage;

    @Nullable
    private IChatComponent message;

    public PlayerChatEvent(Player player, String originalMessage, IChatComponent message) {
        super(player);
        this.originalMessage = originalMessage;
        this.message = message;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    @Nullable
    public IChatComponent getMessage() {
        return message;
    }

    public void setMessage(@Nullable IChatComponent message) {
        this.message = message;
    }
}
