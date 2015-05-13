package nl.jk_5.pumpkin.api.event.server;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;

import nl.jk_5.pumpkin.api.event.Cancellable;
import nl.jk_5.pumpkin.api.event.Event;
import nl.jk_5.pumpkin.api.status.Favicon;
import nl.jk_5.pumpkin.api.status.StatusClient;
import nl.jk_5.pumpkin.api.status.StatusResponse;
import nl.jk_5.pumpkin.api.text.Text;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Called when a client pings the server from the server list.
 * <p>
 * If this event gets cancelled, it will close the client connection without
 * sending any response.
 * </p>
 */
public interface StatusPingEvent extends Event, Cancellable {

    /**
     * Gets the client pinging the server.
     *
     * @return The client of the status request
     */
    StatusClient getClient();

    /**
     * Gets the response that is about to be sent to the client.
     *
     * @return The response to the status request
     */
    Response getResponse();

    /**
     * Represents a mutable response to a status request.
     */
    interface Response extends StatusResponse {

        /**
         * Sets the description (MOTD) of the status response.
         *
         * @param description The description to display
         */
        void setDescription(Text description);

        @Override
        Optional<Players> getPlayers();

        /**
         * Sets whether the player count and the list of players on this server is
         * hidden and doesn't get sent to the client. This will restore
         * {@link #getPlayers()} if the players were previously hidden.
         * <p>
         * Use {@link #getPlayers()}.{@link Optional#isPresent() isPresent()} to
         * check if the players are already hidden.
         * </p>
         * <p>
         * In Vanilla, this will display {@code ???} instead of the player count in
         * the server list.
         * </p>
         *
         * @param hide {@code True} if the players should be hidden
         */
        void setHidePlayers(boolean hide);

        /**
         * Sets the {@link Favicon} to display on the client.
         *
         * @param favicon The favicon, or {@code null} for none
         */
        void setFavicon(@Nullable Favicon favicon);

        /**
         * Represents the information about the players on the server, sent after
         * the {@link StatusPingEvent}.
         */
        interface Players extends StatusResponse.Players {

            /**
             * Sets the amount of online players to display on the client.
             *
             * @param online The amount of online players
             */
            void setOnline(int online);

            /**
             * Sets the maximum amount of allowed players to display on the client.
             *
             * @param max The maximum amount of players
             */
            void setMax(int max);

            /**
             * Gets an mutable list of online players on the server to display on
             * the client.
             *
             * @return A mutable list of online players
             */
            @Override
            List<GameProfile> getProfiles();
        }
    }
}
