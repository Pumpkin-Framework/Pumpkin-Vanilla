package nl.jk_5.pumpkin.server.event.map;

import nl.jk_5.pumpkin.server.player.Player;

public class PlayerLeftMapEvent extends MapEvent {

    private final Player player;

    public PlayerLeftMapEvent(Player player) {
        super(player.getMap());
        this.player = player;
    }
}
