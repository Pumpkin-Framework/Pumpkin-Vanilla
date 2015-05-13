package nl.jk_5.pumpkin.api.status;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;

import nl.jk_5.pumpkin.api.MinecraftVersion;
import nl.jk_5.pumpkin.api.event.server.StatusPingEvent;
import nl.jk_5.pumpkin.api.text.Text;

import java.util.List;

/**
 * Represents the response to a status request. Unlike {@link StatusPingEvent}
 * this is immutable.
 * <p>
 * This interface exists mostly for convenience and can be implemented in a
 * library pinging other servers for example.
 * </p>
 *
 * @see StatusPingEvent
 */
public interface StatusResponse {

    /**
     * Gets the description (MOTD) of the status response.
     *
     * @return The description to display
     */
    Text getDescription();

    /**
     * Gets player count and the list of players currently playing on the
     * server.
     *
     * @return The player information, or {@link Optional#absent()} if not
     *         available
     */
    Optional<? extends Players> getPlayers();

    /**
     * Gets the version of the server displayed when the client or the server
     * are outdated.
     *
     * @return The server version
     */
    MinecraftVersion getVersion();

    /**
     * Gets the {@link Favicon} of the server.
     *
     * @return The favicon, or {@link Optional#absent()} if not available
     */
    Optional<Favicon> getFavicon();

    /**
     * Represents the player count, slots and a list of players current playing
     * on a server.
     */
    interface Players {

        /**
         * Gets the amount of online players on the server.
         *
         * @return The amount of online players
         */
        int getOnline();

        /**
         * Gets the maximum amount of allowed players on the server.
         *
         * @return The maximum amount of allowed players
         */
        int getMax();

        /**
         * Gets an immutable list of online players on the server to display on
         * the client.
         *
         * @return An immutable list of online players
         */
        List<GameProfile> getProfiles();

    }

}
