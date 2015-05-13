package nl.jk_5.pumpkin.api.status;

import java.awt.image.BufferedImage;

/**
 * Represents an icon for the server sent in the {@link StatusResponse}
 */
public interface Favicon {

    /**
     * Gets the decoded image of this favicon.
     *
     * @return The decoded image
     */
    BufferedImage getImage();
}
