package nl.jk_5.pumpkin.api.gamemode;

import nl.jk_5.pumpkin.api.text.translation.Translatable;
import nl.jk_5.pumpkin.api.util.CatalogType;
import nl.jk_5.pumpkin.api.util.annotation.CatalogedBy;

/**
 * Represents a game mode that a {@link nl.jk_5.pumpkin.server.player.Player} may have.
 */
@CatalogedBy(GameModes.class)
public interface GameMode extends CatalogType, Translatable {

}
