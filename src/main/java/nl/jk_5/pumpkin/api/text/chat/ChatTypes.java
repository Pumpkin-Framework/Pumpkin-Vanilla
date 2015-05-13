package nl.jk_5.pumpkin.api.text.chat;

/**
 * ChatTypes is a list of the default chat types that are available in Vanilla
 * Minecraft.
 */
public final class ChatTypes {

    private ChatTypes() {
    }

    /**
     * The standard chat position in prompt at the bottom-left.
     */
    public static final ChatType CHAT = null;

    /**
     * The same position as the {@link #CHAT} position, except messages sent to
     * this position are still seen when chat is turned off on the Minecraft
     * client.
     */
    public static final ChatType SYSTEM = null;

    /**
     * The position right above the inventory, experience, health, item name,
     * etc. bars.
     */
    public static final ChatType ACTION_BAR = null;

}
