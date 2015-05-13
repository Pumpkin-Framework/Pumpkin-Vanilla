package nl.jk_5.pumpkin.api.scoreboard.objective;

import nl.jk_5.pumpkin.api.scoreboard.Score;
import nl.jk_5.pumpkin.api.scoreboard.criteria.Criterion;
import nl.jk_5.pumpkin.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import nl.jk_5.pumpkin.api.text.Text;

import java.util.Map;

/**
 * Represents a builder to create {@link Objective} instances.
 */
public interface ObjectiveBuilder {

    /**
     * Sets the name of the {@link Objective}.
     *
     * @param name The name to set
     * @return This builder
     */
    ObjectiveBuilder name(String name);

    /**
     * Sets the display name of the {@link Objective}.
     *
     * @param displayName The display name to set
     * @return This builder
     */
    ObjectiveBuilder displayName(Text displayName);

    /**
     * Sets the {@link Criterion} of the {@link Objective}.
     *
     * @param criterion The {@link Criterion} to set
     * @return This builder
     */
    ObjectiveBuilder criterion(Criterion criterion);

    /**
     * Sets the {@link ObjectiveDisplayMode} of the {@link Objective}.
     *
     * @param objectiveDisplayMode The {@link ObjectiveDisplayMode} to set
     * @return This builder
     */
    ObjectiveBuilder objectiveDisplayMode(ObjectiveDisplayMode objectiveDisplayMode);

    /**
     * Sets the map of entries and {@link Score}s of the {@link Objective}.
     *
     * <p>By default, this is the empty map.</p>
     *
     * @param entries The map of entries and {@link Score}s to set
     * @return This builder
     */
    ObjectiveBuilder entries(Map<String, Score> entries);

    /**
     * Resets all information regarding the {@link Objective} to be created.
     *
     * @return This builder
     */
    ObjectiveBuilder reset();

    /**
     * Builds an instance of an {@link Objective}.
     *
     * @return A new instance of an {@link Objective}
     * @throws IllegalStateException if the {@link Objective} is not complete
     */
    Objective build() throws IllegalStateException;

}
