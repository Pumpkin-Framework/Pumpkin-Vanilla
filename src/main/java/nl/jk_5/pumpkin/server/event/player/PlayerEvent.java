package nl.jk_5.pumpkin.server.event.player;

import jk_5.eventbus.Event;
import net.minecraft.entity.player.EntityPlayerMP;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
public class PlayerEvent extends Event {

    private final EntityPlayerMP player;

    public PlayerEvent(EntityPlayerMP player) {
        this.player = player;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }
}
