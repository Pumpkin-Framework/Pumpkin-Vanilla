package nl.jk_5.pumpkin.api.text.format;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.util.CatalogType;
import nl.jk_5.pumpkin.api.util.annotation.CatalogedBy;

import java.awt.*;

/**
 * Represents the color of the text of a {@link Text}.
 *
 * @see TextColors
 */
@CatalogedBy(TextColors.class)
public interface TextColor extends CatalogType {

    /**
     * Returns the corresponding {@link Color} for this {@link TextColor}.
     *
     * @return The RGB color of this text color
     */
    Color getColor();

    /**
     * Represents a base {@link TextColor} in Minecraft. It can be represented
     * using legacy formatting codes.
     *
     * @see TextColors
     */
    interface Base extends TextColor, BaseFormatting {

    }

}
