package nl.jk_5.pumpkin.api.effect;

import nl.jk_5.pumpkin.api.text.Text;

public interface ChatListener {

    /**
     * Sends the message(s) to the client.
     *
     * @param messages The message(s) to send
     */
    void sendMessage(Text... messages);

    /**
     * Sends the message(s) to the client.
     *
     * @param messages The message(s) to send
     */
    void sendMessage(Iterable<Text> messages);
}
