package nl.jk_5.pumpkin.api;

/**
 * Represents a specific Minecraft version of a client or a server.
 */
public interface MinecraftVersion extends Comparable<MinecraftVersion> {

    /**
     * Gets the name of this Minecraft version.
     *
     * <p>
     * <strong>Note:</strong> The returned name does not necessarily represent
     * the name of a Minecraft version. Depending on the client and
     * implementation, this may also just return a numeric value.
     * </p>
     *
     * @return The version name
     */
    String getName();

    /**
     * //TODO: create the StatusResponse and StatusPingEvent classes and api functionality
     * Returns whether this version is an older version that doesn't support
     * all of the features in {@link StatusResponse}. These versions are only
     * supported for the {@link StatusPingEvent}, normally they should not be
     * able to join the server.
     * <p>
     * For Vanilla, this returns {@code true} for all clients older than 1.7.
     * </p>
     *
     * @return {@code True} if this version is a legacy version
     */
    boolean isLegacy();
}
