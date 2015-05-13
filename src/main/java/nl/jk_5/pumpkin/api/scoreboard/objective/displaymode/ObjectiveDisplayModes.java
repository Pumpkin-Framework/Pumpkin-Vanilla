package nl.jk_5.pumpkin.api.scoreboard.objective.displaymode;

/**
 * {@link ObjectiveDisplayMode}s which cause scores for an
 * {@link nl.jk_5.pumpkin.api.scoreboard.objective.Objective} to be be
 * displayed differently.
 */
public final class ObjectiveDisplayModes {

    /**
     * Causes the scores for an {@link nl.jk_5.pumpkin.api.scoreboard.objective.Objective}
     * to be displayed as integers.
     */
    public static final ObjectiveDisplayMode INTEGER = null;

    /**
     * Causes the scores for an {@link nl.jk_5.pumpkin.api.scoreboard.objective.Objective}
     * to be displayed as hearts.
     *
     * <p>This only has an effect for an {@link nl.jk_5.pumpkin.api.scoreboard.objective.Objective}
     * with the display slot {@link nl.jk_5.pumpkin.api.scoreboard.displayslot.DisplaySlots#LIST}.</p>
     */
    public static final ObjectiveDisplayMode HEARTS = null;

    private ObjectiveDisplayModes() {
    }

}
