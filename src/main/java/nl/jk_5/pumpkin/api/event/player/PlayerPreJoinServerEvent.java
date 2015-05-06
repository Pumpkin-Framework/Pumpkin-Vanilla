package nl.jk_5.pumpkin.api.event.player;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.server.util.location.Location;

import java.net.InetSocketAddress;
import javax.annotation.Nullable;

public interface PlayerPreJoinServerEvent extends PlayerEvent {

    InetSocketAddress getAddress();

    @Nullable
    ChatComponentText getKickMessage();

    void setKickMessage(@Nullable ChatComponentText kickMessage);

    @Nullable
    IChatComponent getJoinMessage();

    void setJoinMessage(@Nullable IChatComponent joinMessage);

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
