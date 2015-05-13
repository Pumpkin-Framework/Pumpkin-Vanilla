package nl.jk_5.pumpkin.api.scoreboard;

import nl.jk_5.pumpkin.api.scoreboard.objective.Objective;
import nl.jk_5.pumpkin.api.text.Text;

import java.util.Set;

/**
 * A score entry for one or more {@link Objective}s.
 */
public interface Score {

    /**
     * Gets the name of this score.
     *
     * @return The name of this score
     */
    Text getName();

    /**
     * Gets the current score value.
     *
     * @return The current score value
     */
    int getScore();

    /**
     * Sets the current score value.
     *
     * @param score The new score value
     */
    void setScore(int score);

    /**
     * Returns a {@link Set} of parent {@link Objective}s this {@link Score} is
     * registered to.
     *
     * @return A {@link Set} of parent {@link Objective} this {@link Score} is
     *         registered to
     */
    Set<Objective> getObjectives();

}
