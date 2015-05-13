package nl.jk_5.pumpkin.api.scoreboard;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.format.TextColor;
import nl.jk_5.pumpkin.server.player.Player;

import java.util.Set;

/**
 * A team on a scoreboard that has a common display theme and other
 * properties.
 */
public interface Team {

    /**
     * Gets the name of this team.
     *
     * @return The name of this team
     */
    String getName();

    /**
     * Gets the name displayed to users for this team.
     *
     * @return The display name for this team
     */
    Text getDisplayName();

    /**
     * Gets the color of this team.
     *
     * <p>The team's color is a distinct concept from its prefix or suffix.
     * It is only used for colored sidebar display slots, and certain statistic
     * criteria.</p>
     *
     * @return The team color
     */
    TextColor getColor();

    /**
     * Sets the color of this team.
     *
     * <p>The team's color is a distinct concept from its prefix or suffix.
     * It is only used for colored sidebar display slots, and certain statistic
     * criteria.</p>
     *
     * @param color The team color
     * @throws IllegalArgumentException If color is {@link nl.jk_5.pumpkin.api.text.format.TextColors#RESET}
     */
    void setColor(TextColor color) throws IllegalArgumentException;

    /**
     * Sets the name displayed to users for this team.
     *
     * @param displayName The {@link Text} to use
     * @throws IllegalArgumentException If displayName is longer than 32
     *     characters
     */
    void setDisplayName(Text displayName) throws IllegalArgumentException;

    /**
     * Gets the prefix prepended to the display name of users on this team.
     *
     * @return The prefix for this team
     */
    Text getPrefix();

    /**
     * Sets the prefix prepended to the display name of users on this team.
     *
     * @param prefix The new prefix for this team
     * @throws IllegalArgumentException If prefix is longer than 16
     *     characters
     */
    void setPrefix(Text prefix) throws IllegalArgumentException;

    /**
     * Gets the suffix appended to the display name of users on this team.
     *
     * @return The team's current suffix
     */
    Text getSuffix();

    /**
     * Sets the suffix appended to the display name of users on this team.
     *
     * @param suffix The new suffix for this team.
     * @throws IllegalArgumentException If suffix is longer than 16
     *     characters
     */
    void setSuffix(Text suffix) throws IllegalArgumentException;

    /**
     * Gets whether friendly fire is enabled.
     *
     * @return Whether friendly fire is enabled
     */
    boolean allowFriendlyFire();

    /**
     * Sets whether friendly fire is enabled.
     *
     * @param enabled Whether friendly fire is enabled
     */
    void setAllowFriendlyFire(boolean enabled);

    /**
     * Gets whether invisible team members are shown.
     *
     * @return Whether to show invisible team members
     */
    boolean canSeeFriendlyInvisibles();

    /**
     * Sets whether invisible team members are shown.
     *
     * @param enabled Whether to show invisible teammates
     */
    void setCanSeeFriendlyInvisibles(boolean enabled);

    /**
     * Gets the {@link Visibility} which controls to who nametags
     * of players on this team are visible to.
     *
     * @return The {@link Visibility} for this team's nametags
     */
    Visibility getNameTagVisibility();

    /**
     * Sets the {@link Visibility} which controls to who nametags
     * of players on this team are visible to.
     *
     * @param visibility The {@link Visibility} for this team's nametags
     */
    void setNameTagVisibility(Visibility visibility);

    /**
     * Gets the {@link Visibility} which controls who death Texts
     * for players on this team are visible to.
     *
     * @return The {@link Visibility} for this team's death Texts
     */
    Visibility getDeathTextVisibility();

    /**
     * Sets the {@link Visibility} which controls who death Texts
     * of players on this team are visible to.
     *
     * @param visibility The {@link Visibility} for this team's death Texts
     */
    void setDeathTextVisibility(Visibility visibility);

    /**
     * Gets the {@link Player}s on the team.
     *
     * @return The {@link Player}s on the team
     */
    Set<Player> getPlayers();

    /**
     * Adds the specified {@link Player} to this team for the {@link Scoreboard}.
     *
     * <p>This will remove the {@link Player} from any other team on the {@link Scoreboard}.</p>
     *
     * @param player The {@link Player} to add
     */
    void addPlayer(Player player);

    /**
     * Removes the specified {@link Player} from this team.
     *
     * @param player The {@link Player} to remove
     * @return Whether the {@link Player} was on this team
     */
    boolean removePlayer(Player player);

    /**
     * Returns a {@link Set} of parent {@link Scoreboard}s this {@link Team} is
     * registered to.
     *
     * @return A {@link Set} of parent {@link Scoreboard}s this {@link Team} is
     *         registered to
     */
    Set<Scoreboard> getScoreboards();

}
