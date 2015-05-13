package nl.jk_5.pumpkin.server.mixin.interfaces.text;

import nl.jk_5.pumpkin.api.text.action.ClickAction;

public interface IMixinClickEvent {

    ClickAction<?> getHandle();

    void setHandle(ClickAction<?> handle);

}
