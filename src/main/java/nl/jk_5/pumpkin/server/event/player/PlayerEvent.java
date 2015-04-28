package nl.jk_5.pumpkin.server.event.player;

import nl.jk_5.eventbus.Event;
import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public class PlayerEvent extends Event {

    private final Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Nullable
    public User getUser(){
        return player.getUser();
    }
}
