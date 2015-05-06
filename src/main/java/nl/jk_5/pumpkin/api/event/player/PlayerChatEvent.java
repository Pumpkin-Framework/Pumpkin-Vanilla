package nl.jk_5.pumpkin.api.event.player;

import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.api.event.Cancellable;

import javax.annotation.Nullable;

public interface PlayerChatEvent extends PlayerEvent, Cancellable {

    String getOriginalMessage();

    @Nullable
    IChatComponent getMessage();

    void setMessage(@Nullable IChatComponent message);
}
