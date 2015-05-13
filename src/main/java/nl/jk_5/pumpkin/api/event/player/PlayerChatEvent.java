package nl.jk_5.pumpkin.api.event.player;

import nl.jk_5.pumpkin.api.event.Cancellable;
import nl.jk_5.pumpkin.api.text.Text;

import javax.annotation.Nullable;

public interface PlayerChatEvent extends PlayerEvent, Cancellable {

    String getOriginalMessage();

    @Nullable
    Text getMessage();

    void setMessage(@Nullable Text message);
}
