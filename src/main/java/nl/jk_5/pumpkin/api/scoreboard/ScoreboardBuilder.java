package nl.jk_5.pumpkin.api.scoreboard;

import nl.jk_5.pumpkin.api.scoreboard.objective.Objective;

import java.util.List;

/**
 * Represents a builder to create {@link Scoreboard} instances.
 */
public interface ScoreboardBuilder {

    /**
     * Sets the list of {@link Objective}s of the {@link Scoreboard}.
     *
     * <p>By default, this is the empty list.</p>
     *
     * @param objectives The list of {@link Objective}s to set
     * @return This builder
     */
    ScoreboardBuilder objectives(List<Objective> objectives);

    /**
     * Sets the list of {@link Team}s of the {@link Scoreboard}.
     *
     * <p>By default, this is the empty list.</p>
     *
     * @param teams The list of {@link Team}s to set
     * @return This builder
     */
    ScoreboardBuilder teams(List<Team> teams);

    /**
     * Resets all information regarding the {@link Scoreboard} to be created.
     *
     * @return This builder
     */
    ScoreboardBuilder reset();

    /**
     * Builds an instance of a {@link Scoreboard}.
     *
     * @return A new instance of a {@link Scoreboard}
     * @throws IllegalStateException if the {@link Scoreboard} is not complete
     */
    Scoreboard build() throws IllegalStateException;

}
