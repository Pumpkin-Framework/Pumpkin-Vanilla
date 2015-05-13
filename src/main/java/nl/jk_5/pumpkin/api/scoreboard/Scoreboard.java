package nl.jk_5.pumpkin.api.scoreboard;

import com.google.common.base.Optional;

import nl.jk_5.pumpkin.api.scoreboard.criteria.Criterion;
import nl.jk_5.pumpkin.api.scoreboard.displayslot.DisplaySlot;
import nl.jk_5.pumpkin.api.scoreboard.objective.Objective;
import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.server.player.Player;

import java.util.Set;
import javax.annotation.Nullable;

/**
 * Represents a scoreboard, which contains {@link Team}s and {@link Objective}s.
 * The server has a default scoreboard, but each {@link Player}
 * can have their own scoreboard.
 *
 * @see <a href="http://minecraft.gamepedia.com/Scoreboard">Scoreboards on the Minecraft Wiki</a>
 */
public interface Scoreboard {

    /**
     * Gets an {@link Objective} on this scoreboard by name, if it exists.
     *
     * @param name Name of the {@link Objective}
     * @return The {@link Objective}, if it exists
     */
    Optional<Objective> getObjective(String name);

    /**
     * Gets the {@link Objective} currently displayed in a {@link DisplaySlot} on this
     * scoreboard, if one is present.
     *
     * @param slot The {@link DisplaySlot}
     * @return the {@link Objective} currently displayed, if present
     */
    Optional<Objective> getObjective(DisplaySlot slot);

    /**
     * Sets the specified {@link Objective} in the specified {@link DisplaySlot}, removing
     * it from any other {@link DisplaySlot}.
     *
     * <p>If another objective is set to the same display slot, that objective will
     * have it's display slot set to <code>null</code>.</p>
     *
     * @param objective The {@link Objective} to set
     * @param displaySlot The {@link DisplaySlot} to the specified {@link Objective} in
     * @throws IllegalStateException if the specified {@link Objective} does not exist
     *                               on this scoreboard
     */
    void addObjective(Objective objective, @Nullable DisplaySlot displaySlot) throws IllegalStateException;

    /**
     * Adds the specified {@link Objective} to this scoreboard.
     *
     * @param objective The {@link Objective} add
     * @throws IllegalArgumentException if an {@link Objective} with the same
     *             {@link Objective#getName() name} already exists, or if the
     *             specified {@link Objective} has already been added.
     */
    void addObjective(Objective objective) throws IllegalArgumentException;

    /**
     * Gets all {@link Objective}s of a Criteria on this scoreboard.
     *
     * @param criteria {@link Criterion} to search by
     * @return A set of {@link Objective}s using the specified Criteria
     */
    Set<Objective> getObjectivesByCriteria(Criterion criteria);

    /**
     * Gets all {@link Objective}s on this scoreboard.
     *
     * @return A set of all {@link Objective}s on this scoreboard
     */
    Set<Objective> getObjectives();

    /**
     * Removes the specified {@link Objective} from this scoreboard.
     *
     * @param objective The {@link Objective} to remove
     */
    void removeObjective(Objective objective);

    /**
     * Gets all scores with the specified name on this scoreboard,
     * across all objectives.
     *
     * @param name The name whose scores are being retrieved
     * @return A set of all scores for the name
     */
    Set<Score> getScores(Text name);

    /**
     * Removes all scores with the specified name on this scoreboard,
     * across all objectives.
     *
     * @param name The name to remove all scores for
     */
    void removeScores(Text name);

    /**
     * Gets a {@link Player}'s {@link Team}s on this scoreboard.
     *
     * @param user The {@link Player} to search for
     * @return The {@link Player}'s {@link Team}s, if the user has any
     *         {@link Team}s
     */
    Set<Team> getPlayerTeams(Player user);

    /**
     * Gets a {@link Team} by name on this scoreboard.
     *
     * @param teamName The name of the {@link Team}
     * @return The matching {@link Team}, if it exists
     */
    Optional<Team> getTeam(String teamName);

    /**
     * Removes the specified {@link Team} to this scoreboard.
     *
     * @param team The {@link Team} to remove
     */
    void removeTeam(Team team);

    /**
     * Adds the specified {@link Team} to this scoreboard.
     *
     * @param team The {@link Team} to add
     * @throws IllegalArgumentException if a team with the same
     *             {@link Team#getName() name} already exists, or the specified
     *             {@link Team} has been added
     */
    void addTeam(Team team) throws IllegalArgumentException;

    /**
     * Gets all the {@link Team}s on this scoreboard.
     *
     * @return The set of {@link Team}s
     */
    Set<Team> getTeams();

    /**
     * Clears any {@link Objective} in the specified slot.
     *
     * @param slot The {@link DisplaySlot} to remove any {@link Objective} for
     */
    void clearSlot(DisplaySlot slot);

}