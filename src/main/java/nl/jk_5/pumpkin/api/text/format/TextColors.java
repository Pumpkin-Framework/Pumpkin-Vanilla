package nl.jk_5.pumpkin.api.text.format;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.awt.*;

/**
 * TextColors is a list of text colors provided by Vanilla Minecraft.
 */
@NonnullByDefault
public final class TextColors {

    private TextColors() {
    }

    /**
     * Represents a base color that is used as default if no color is specified.
     * This will result in either the default color of the receiver or inherit
     * it from a parent {@link Text}.
     */
    public static final TextColor NONE = new TextColor() {

        private final Color color = new Color(0, 0, 0, 0);

        @Override
        public String getName() {
            return "NONE";
        }

        @Override
        public Color getColor() {
            return this.color;
        }

        @Override
        public String getId() {
            return "NONE";
        }
    };

    public static final TextColor.Base BLACK = null;
    public static final TextColor.Base DARK_BLUE = null;
    public static final TextColor.Base DARK_GREEN = null;
    public static final TextColor.Base DARK_AQUA = null;
    public static final TextColor.Base DARK_RED = null;
    public static final TextColor.Base DARK_PURPLE = null;
    public static final TextColor.Base GOLD = null;
    public static final TextColor.Base GRAY = null;
    public static final TextColor.Base DARK_GRAY = null;
    public static final TextColor.Base BLUE = null;
    public static final TextColor.Base GREEN = null;
    public static final TextColor.Base AQUA = null;
    public static final TextColor.Base RED = null;
    public static final TextColor.Base LIGHT_PURPLE = null;
    public static final TextColor.Base YELLOW = null;
    public static final TextColor.Base WHITE = null;
    
    /**
     * Resets the current color to the default one on the client. In most cases
     * this should be the same as {@link #WHITE}.
     */
    public static final TextColor.Base RESET = null;

}
