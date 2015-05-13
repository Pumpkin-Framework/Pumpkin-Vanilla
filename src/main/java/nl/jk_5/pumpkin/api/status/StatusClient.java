package nl.jk_5.pumpkin.api.status;

import com.google.common.base.Optional;

import nl.jk_5.pumpkin.api.MinecraftVersion;

import java.net.InetSocketAddress;

/**
 * Represents a client requesting a {@link StatusResponse}. Unlike normal player
 * connections, it may not have the same version as the server.
 */
public interface StatusClient {

    /**
     * Gets the address of the client.
     *
     * @return The address of the client
     */
    InetSocketAddress getAddress();

    /**
     * Gets the game version of the client.
     *
     * @return The version of the client
     */
    MinecraftVersion getVersion();

    /**
     * Gets the address the player is connecting to.
     *
     * @return The address the player is connecting to, or
     *         {@link Optional#absent()} if not available (for example because
     *         of {@link MinecraftVersion#isLegacy()}).
     */
    Optional<InetSocketAddress> getVirtualHost();

}
