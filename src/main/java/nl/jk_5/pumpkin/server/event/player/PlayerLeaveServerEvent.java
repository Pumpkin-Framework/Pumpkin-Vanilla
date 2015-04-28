package nl.jk_5.pumpkin.server.event.player;

import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public class PlayerLeaveServerEvent extends PlayerEvent {

    @Nullable
    private IChatComponent leaveMessage;

    public PlayerLeaveServerEvent(Player player, IChatComponent leaveMessage) {
        super(player);
        this.leaveMessage = leaveMessage;
    }

    @Nullable
    public IChatComponent getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(@Nullable IChatComponent leaveMessage) {
        this.leaveMessage = leaveMessage;
    }
}
