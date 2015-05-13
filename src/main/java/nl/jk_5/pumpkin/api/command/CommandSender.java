package nl.jk_5.pumpkin.api.command;

import nl.jk_5.pumpkin.api.effect.ChatListener;

public interface CommandSender extends ChatListener {

    String getName();
}
