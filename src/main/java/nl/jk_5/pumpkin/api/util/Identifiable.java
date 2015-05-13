package nl.jk_5.pumpkin.api.util;

import java.util.UUID;

/**
 * An identifiable object has a UUID that can be retrieved.
 */
public interface Identifiable {

    /**
     * Gets the unique ID for this object.
     *
     * @return The {@link UUID}
     */
    UUID getUniqueId();

}
