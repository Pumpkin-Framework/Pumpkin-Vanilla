package nl.jk_5.pumpkin.api.scoreboard;

import nl.jk_5.pumpkin.api.util.CatalogType;
import nl.jk_5.pumpkin.api.util.annotation.CatalogedBy;

/**
 * Represents a group or groups players to display something to.
 *
 * <p>Usages include nametags and death messages.</p>
 */
@CatalogedBy(Visibilities.class)
public interface Visibility extends CatalogType {

}
