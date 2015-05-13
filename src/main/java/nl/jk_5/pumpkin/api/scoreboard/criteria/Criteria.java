package nl.jk_5.pumpkin.api.scoreboard.criteria;

/**
 * Criteria names which trigger an objective to be modified by actions in-game.
 */
public final class Criteria {

    /**
     * Represents a {@link Criterion} which causes an {@link nl.jk_5.pumpkin.api.scoreboard.objective.Objective}
     * is only updated manually, through commands or plugins.
     */
    public static final Criterion DUMMY = null;

    /**
     * Represents a {@link Criterion} which causes an {@link nl.jk_5.pumpkin.api.scoreboard.objective.Objective}
     * to have a score for a player updated by the <code>/trigger</code> command.
     */
    public static final Criterion TRIGGER = null;

    /**
     * Represents a {@link Criteria} which causes an {@link nl.jk_5.pumpkin.api.scoreboard.objective.Objective}
     * to have a score for a player represent their current health,
     * on a scale of 0-20 (can be greater than 20 due to effects
     * such as {@link nl.jk_5.pumpkin.api.potion.PotionEffectTypes#HEALTH_BOOST}
     */
    public static final Criterion HEALTH = null;

    /**
     * Represents a {@link Criteria} which causes an {@link nl.jk_5.pumpkin.api.scoreboard.objective.Objective}
     * to have a score for a player incremented when they kill a player.
     */
    public static final Criterion PLAYER_KILLS = null;

    /**
     * Represents a {@link Criteria} which causes an {@link nl.jk_5.pumpkin.api.scoreboard.objective.Objective}
     * to have a score for a player incremented when they kill an entity.
     */
    public static final Criterion TOTAL_KILLS = null;

    /**
     * Represents a {@link Criteria} which causes an {@link nl.jk_5.pumpkin.api.scoreboard.objective.Objective}
     * to have a score for a player incremented when they die.
     */
    public static final Criterion DEATHS = null;

    private Criteria() {
    }

}
