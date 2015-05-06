package nl.jk_5.pumpkin.api.event.player;

import nl.jk_5.pumpkin.api.event.user.UserEvent;
import nl.jk_5.pumpkin.server.player.Player;

public interface PlayerEvent extends UserEvent {

    Player getPlayer();
}
