package nl.jk_5.pumpkin.api.event.user;

import nl.jk_5.pumpkin.api.event.Event;
import nl.jk_5.pumpkin.api.user.User;

import javax.annotation.Nullable;

public interface UserEvent extends Event {

    @Nullable
    User getUser();
}
