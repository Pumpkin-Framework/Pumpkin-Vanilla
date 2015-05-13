package nl.jk_5.pumpkin.api.scoreboard.criteria;

import nl.jk_5.pumpkin.api.util.CatalogType;
import nl.jk_5.pumpkin.api.util.annotation.CatalogedBy;

/**
 * Represents a set of behaviours for an objective, which may cause it to be automatically updated.
 */
@CatalogedBy(Criteria.class)
public interface Criterion extends CatalogType {

    /**
     * Gets the name of this criterion.
     *
     * @return The name of this criterion
     */
    @Override
    String getName();

}
