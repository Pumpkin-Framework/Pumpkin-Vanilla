package nl.jk_5.pumpkin.api.net;

import nl.jk_5.pumpkin.server.player.Player;

import java.net.InetSocketAddress;

/**
 * Represents a connection of a client to the server.
 */
public interface PlayerConnection {

    /**
     * Gets the associated {@link Player} for this connection.
     *
     * @return The associated player
     */
    Player getPlayer();

    /**
     * Gets the {@link InetSocketAddress} of this connection.
     *
     * @return The address
     */
    InetSocketAddress getAddress();

    /**
     * Gets the host name the connection is connecting to the server with.
     *
     * <p>Examples include: If a player is connecting to `mc.example.com`,
     * the hostname will show `mc.example.com`. This is NOT the originating
     * hostname of the client.</p>
     *
     * @return The host name
     */
    InetSocketAddress getVirtualHost();

    /**
     * Gets the connection ping. This is constantly calculated by the server.
     *
     * @return The ping
     */
    int getPing();
}
