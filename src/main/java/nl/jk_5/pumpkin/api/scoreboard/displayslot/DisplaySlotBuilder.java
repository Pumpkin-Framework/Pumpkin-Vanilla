package nl.jk_5.pumpkin.api.scoreboard.displayslot;

import nl.jk_5.pumpkin.api.text.format.TextColor;

public interface DisplaySlotBuilder {

    /**
     * Sets the {@link TextColor} of the display slot.
     *
     * @param color The color to set
     * @return This builder
     */
    DisplaySlotBuilder sidebarTeamColor(TextColor color);

    /**
     * Builds an instance of a {@link DisplaySlot}.
     *
     * @return A new instance of an {@link DisplaySlot}
     * @throws IllegalStateException if the {@link DisplaySlot} is not completed
     */
    DisplaySlot build() throws IllegalStateException;

}
