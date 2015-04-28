package nl.jk_5.pumpkin.server.event.player;

import org.apache.commons.lang3.Validate;

import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;
import nl.jk_5.pumpkin.server.util.location.Location;

@NonnullByDefault
public class PlayerRespawnEvent extends PlayerEvent {

    protected PlayerRespawnEvent(Player player) {
        super(player);
    }

    @NonnullByDefault
    public static class Pre extends PlayerRespawnEvent {

        private final Location deathLocation;
        private Location respawnLocation;

        public Pre(Player player, Location deathLocation, Location respawnLocation) {
            super(player);
            this.deathLocation = deathLocation;
            this.respawnLocation = respawnLocation;
        }

        public Location getDeathLocation() {
            return deathLocation;
        }

        public Location getRespawnLocation() {
            return respawnLocation;
        }

        public void setRespawnLocation(Location respawnLocation) {
            Validate.notNull(respawnLocation, "respawnLocation may not be null");
            this.respawnLocation = respawnLocation;
        }
    }

    @NonnullByDefault
    public static class Post extends PlayerRespawnEvent {

        public Post(Player player) {
            super(player);
        }
    }
}
