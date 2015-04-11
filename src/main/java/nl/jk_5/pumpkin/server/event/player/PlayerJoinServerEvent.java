package nl.jk_5.pumpkin.server.event.player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;

import nl.jk_5.pumpkin.server.util.Location;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import javax.annotation.Nullable;

@NonnullByDefault
public class PlayerJoinServerEvent extends PlayerEvent {

    protected PlayerJoinServerEvent(EntityPlayerMP player) {
        super(player);
    }

    @NonnullByDefault
    public static class Pre extends PlayerJoinServerEvent {

        private final InetSocketAddress address;
        @Nullable private ChatComponentText kickMessage;
        @Nullable private IChatComponent joinMessage;
        private Location spawnPoint;
        @Nullable private Location location;

        public Pre(EntityPlayerMP player, SocketAddress address, IChatComponent joinMessage, Location spawnPoint, Location location) {
            super(player);
            this.address = (InetSocketAddress) address;
            this.joinMessage = joinMessage;
            this.spawnPoint = spawnPoint;
            this.location = location;
        }

        public InetSocketAddress getAddress() {
            return address;
        }

        public void preventLogin(@Nullable ChatComponentText kickMessage) {
            this.kickMessage = kickMessage;
        }

        @Nullable
        public ChatComponentText getKickMessage() {
            return kickMessage;
        }

        @Nullable
        public IChatComponent getJoinMessage() {
            return joinMessage;
        }

        public void setJoinMessage(@Nullable IChatComponent joinMessage) {
            this.joinMessage = joinMessage;
        }

        public Location getSpawnPoint() {
            return spawnPoint;
        }

        /**
         * Spawnpoint is only used when the player joins the server for the first time.
         * If you want to force the location, use setLocatoin
         */
        public void setSpawnPoint(Location spawnPoint) {
            Validate.notNull(spawnPoint, "spawnPoint may not be null");
            this.spawnPoint = spawnPoint;
        }

        @Nullable
        public Location getLocation() {
            return location;
        }

        public void setLocation(@Nullable Location location) {
            this.location = location;
        }
    }

    @NonnullByDefault
    public static class Post extends PlayerJoinServerEvent {

        public Post(EntityPlayerMP player) {
            super(player);
        }
    }
}
