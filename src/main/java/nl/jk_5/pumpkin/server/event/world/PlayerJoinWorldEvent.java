package nl.jk_5.pumpkin.server.event.world;

import nl.jk_5.pumpkin.server.player.Player;

public class PlayerJoinWorldEvent extends WorldEvent {

    private final Player player;

    public PlayerJoinWorldEvent(Player player) {
        super(player.getWorld());
        this.player = player;
    }
}
