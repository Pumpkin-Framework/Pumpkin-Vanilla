package nl.jk_5.pumpkin.api.scoreboard.displayslot;

import com.google.common.base.Optional;

import nl.jk_5.pumpkin.api.text.format.TextColor;
import nl.jk_5.pumpkin.api.util.CatalogType;
import nl.jk_5.pumpkin.api.util.annotation.CatalogedBy;

/**
 * Represents an area to display an objective.
 */
@CatalogedBy(DisplaySlots.class)
public interface DisplaySlot extends CatalogType {

    /**
     * Gets the {@link nl.jk_5.pumpkin.api.scoreboard.Team} color that this objective will display for, if set.
     *
     * @return The {@link nl.jk_5.pumpkin.api.scoreboard.Team} color that this objective will display for, if set
     */
    Optional<TextColor> getTeamColor();

}
