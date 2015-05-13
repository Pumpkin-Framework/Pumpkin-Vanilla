package nl.jk_5.pumpkin.api.text.title;

import nl.jk_5.pumpkin.api.text.Text;

/**
 * Utility class to create instances of {@link TitleBuilder}.
 */
public final class Titles {

    public static final Title EMPTY = new Title();
    public static final Title CLEAR = new Title(null, null, null, null, null, true, false);
    public static final Title RESET = new Title(null, null, null, null, null, false, true);

    private Titles() {
    }

    /**
     * Returns a {@link Title} that will simply do nothing when it is sent to
     * the client.
     *
     * @return An empty title instance
     */
    public static Title of() {
        return EMPTY;
    }

    /**
     * Returns a {@link Title} that will display the given main title on the
     * player's screen.
     *
     * @param title The title to display
     * @return The created title
     */
    public static Title of(Text title) {
        return builder().title(title).build();
    }

    /**
     * Returns a {@link Title} that will display the given main and subtitle on
     * the player's screen.
     *
     * @param title The title to display
     * @param subtitle The subtitle to display
     * @return The created title
     */
    public static Title of(Text title, Text subtitle) {
        return builder().title(title).subtitle(subtitle).build();
    }

    /**
     * Returns a {@link Title} that will clear the currently displayed
     * {@link Title} from the player's screen.
     *
     * @return A title configuration that will clear
     */
    public static Title clear() {
        return CLEAR;
    }

    /**
     * Returns a {@link Title} that will reset the current title back to default
     * values on the client.
     *
     * @return A title configuration that will reset
     */
    public static Title reset() {
        return RESET;
    }

    /**
     * Creates a new {@link Title} configuration builder that will reset the
     * currently displayed Title on the client before displaying the new
     * configured one.
     *
     * @return A new {@link TitleBuilder}
     * @see #update
     */
    public static TitleBuilder builder() {
        return update().reset();
    }

    /**
     * Creates a new empty {@link Title} configuration builder. Unlike
     * {@link #builder} this won't reset the current Title on the client before
     * displaying the current one. This has less use cases but should be used if
     * just the previously sent Title should be updated.
     *
     * @return A new {@link TitleBuilder}
     * @see #builder
     */
    public static TitleBuilder update() {
        return new TitleBuilder();
    }

}
