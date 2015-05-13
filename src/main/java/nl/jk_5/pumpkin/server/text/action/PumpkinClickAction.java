package nl.jk_5.pumpkin.server.text.action;

import net.minecraft.event.ClickEvent;

import nl.jk_5.pumpkin.api.text.action.ClickAction;

public final class PumpkinClickAction {

    private PumpkinClickAction() {}

    private static ClickEvent.Action getType(ClickAction<?> action) {
        if (action instanceof ClickAction.OpenUrl) {
            return ClickEvent.Action.OPEN_URL;
        } else if (action instanceof ClickAction.RunCommand) {
            return ClickEvent.Action.RUN_COMMAND;
        } else if (action instanceof ClickAction.SuggestCommand) {
            return ClickEvent.Action.SUGGEST_COMMAND;
        } else if (action instanceof ClickAction.ChangePage) {
            return ClickEvent.Action.CHANGE_PAGE;
        }

        throw new UnsupportedOperationException(action.getClass().toString());
    }

    public static ClickEvent getHandle(ClickAction<?> action) {
        return new ClickEvent(getType(action), action.getResult().toString());
    }

}
