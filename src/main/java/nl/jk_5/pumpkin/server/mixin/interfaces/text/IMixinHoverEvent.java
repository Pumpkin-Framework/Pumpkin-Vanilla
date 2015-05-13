package nl.jk_5.pumpkin.server.mixin.interfaces.text;

import nl.jk_5.pumpkin.api.text.action.HoverAction;

public interface IMixinHoverEvent {

    HoverAction<?> getHandle();

    void setHandle(HoverAction<?> handle);

}
