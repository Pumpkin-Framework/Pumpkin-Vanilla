package nl.jk_5.pumpkin.api.scoreboard.objective;

import nl.jk_5.pumpkin.api.scoreboard.Score;
import nl.jk_5.pumpkin.api.scoreboard.Scoreboard;
import nl.jk_5.pumpkin.api.scoreboard.criteria.Criterion;
import nl.jk_5.pumpkin.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import nl.jk_5.pumpkin.api.text.Text;

import java.util.Set;

/**
 * An objective tracks an integer score for each entry it contains.
 *
 * <p>Entries can be updated by plugins, by in-game commands, or automatically by the game, depending
 * on their {@link Criterion}.</p>
 */
public interface Objective {

    /**
     * Gets the name of this Objective.
     *
     * @return The objective's name
     */
    String getName();

    /**
     * Gets the name displayed to players.
     *
     * @return The objective's display name
     */
    Text getDisplayName();

    /**
     * Sets the name displayed to players.
     *
     * @param displayName Display name to set
     * @throws IllegalArgumentException if displayName is longer than 32
     *     characters
     */
    void setDisplayName(Text displayName) throws IllegalArgumentException;

    /**
     * Gets the criterion for this objective.
     *
     * @return This objective's criterion
     */
    Criterion getCriterion();

    /**
     * Gets the {@link ObjectiveDisplayMode} used to display this objective.
     *
     * @return The {@link ObjectiveDisplayMode} used to display this objective
     */
    ObjectiveDisplayMode getDisplayMode();

    /**
     * Sets the {@link ObjectiveDisplayMode} used to display this objective.
     *
     * @param displayMode The {@link ObjectiveDisplayMode} used to display this objective
     */
    void setDisplayMode(ObjectiveDisplayMode displayMode);

    /**
     * Gets the set of {@link Score}s for this objective.
     *
     * @return The set of {@link Score}s for this objective
     */
    Set<Score> getScores();

    /**
     * Adds the specified {@link Score} to this objective.
     *
     * @param score The {@link Score} to add
     * @throws IllegalArgumentException If a {@link Score} with the same name exists, or the specified {@link Score} has already been added
     */
    void addScore(Score score) throws IllegalArgumentException;

    /**
     * Gets an entry's {@link Score} for this Objective.
     *
     * <p>If the {@link Score} does not exist, it will be created.</p>
     *
     * @param name The name of the {@link Score} to get
     * @return The {@link Score} for the specified {@link Text}
     */
    Score getScore(Text name);

    /**
     * Removes the specified {@link Score} to this objective.
     *
     * @param score The {@link Score} to remove
     */
    void removeScore(Score score);

    /**
     * Returns a {@link Set} of parent {@link Scoreboard}s this
     * {@link Objective} is registered to.
     *
     * @return A {@link Set} of parent {@link Scoreboard}s this
     *         {@link Objective} is registered to
     */
    Set<Scoreboard> getScoreboards();

}
