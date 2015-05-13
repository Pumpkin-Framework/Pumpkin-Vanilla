package nl.jk_5.pumpkin.api.scoreboard;

/**
 * Visibility names which cause nametags or death messages to be displayed
 * differently to players on a team.
 */
public final class Visibilities {

    /**
     * Displays nametags or death messages for all players.
     */
    public static final Visibility ALL = null;

    /**
     * Displays nametags or death messages for players on their own team.
     */
    public static final Visibility OWN_TEAM = null;

    /**
     * Displays nametags or death messages for all players on teams other
     * than their team.
     */
    public static final Visibility OTHER_TEAMS = null;

    /**
     * Displays nametags or death messages for no players.
     */
    public static final Visibility NONE = null;

    private Visibilities() {
    }

}
