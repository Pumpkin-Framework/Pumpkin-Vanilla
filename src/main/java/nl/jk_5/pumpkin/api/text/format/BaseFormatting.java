package nl.jk_5.pumpkin.api.text.format;

/**
 * Represents a text formatting with a name and a deprecated legacy code.
 */
public interface BaseFormatting {

    /**
     * Returns the corresponding Minecraft name for this {@link BaseFormatting}.
     *
     * @return The Minecraft name for this formatting
     */
    String getName();

    /**
     * Gets the corresponding Minecraft formatting code.
     *
     * @return The Minecraft formatting code of this format
     */
    @Deprecated
    char getCode();

}
