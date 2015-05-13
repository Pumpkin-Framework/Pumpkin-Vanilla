package nl.jk_5.pumpkin.api.event.player;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.server.util.location.Location;

import java.net.InetSocketAddress;
import javax.annotation.Nullable;

public interface PlayerPreJoinServerEvent extends PlayerEvent {

    InetSocketAddress getAddress();

    @Nullable
    Text getKickMessage();

    void setKickMessage(@Nullable Text kickMessage);

    @Nullable
    Text getJoinMessage();

    void setJoinMessage(@Nullable Text joinMessage);

    Location getSpawnPoint();

    /**
     * Spawnpoint is only used when the player joins the server for the first time.
     * If you want to force the location, use setLocatoin
     */
    void setSpawnPoint(Location spawnPoint);

    @Nullable
    Location getLocation();

    void setLocation(@Nullable Location location);
}
