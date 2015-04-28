package nl.jk_5.pumpkin.server.event.world;

import nl.jk_5.pumpkin.server.player.Player;

public class PlayerLeftWorldEvent extends WorldEvent {

    private final Player player;

    public PlayerLeftWorldEvent(Player player) {
        super(player.getWorld());
        this.player = player;
    }
}
