package nl.jk_5.pumpkin.server.event.map;

import nl.jk_5.pumpkin.server.player.Player;

public class PlayerJoinMapEvent extends MapEvent {

    public PlayerJoinMapEvent(Player player) {
        super(player.getMap());
    }
}
