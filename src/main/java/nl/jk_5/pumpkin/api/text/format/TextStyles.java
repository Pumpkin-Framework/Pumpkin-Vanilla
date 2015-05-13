package nl.jk_5.pumpkin.api.text.format;

/**
 * Represents a list of the text styles provided by Vanilla Minecraft.
 */
public final class TextStyles {

    private TextStyles() {
    }

    /**
     * Represents an empty {@link TextStyle}.
     */
    public static final TextStyle NONE = new TextStyle();

    public static final TextStyle.Base OBFUSCATED = null;
    public static final TextStyle.Base BOLD = null;
    public static final TextStyle.Base STRIKETHROUGH = null;
    public static final TextStyle.Base UNDERLINE = null;
    public static final TextStyle.Base ITALIC = null;

    /**
     * Represents a {@link TextStyle} with all bases set to {@code false}.
     */
    public static final TextStyle.Base RESET = null;

    /**
     * Returns an empty {@link TextStyle}.
     *
     * @return An empty text style
     */
    public static TextStyle of() {
        return NONE;
    }

    /**
     * Constructs a composite text style from the specified styles. This will
     * result in the same as calling {@link TextStyle#and(TextStyle...)} on all
     * of the text styles.
     *
     * @param styles The styles to combine
     * @return A composite text style from the specified styles
     */
    public static TextStyle of(TextStyle... styles) {
        return NONE.and(styles);
    }

}
