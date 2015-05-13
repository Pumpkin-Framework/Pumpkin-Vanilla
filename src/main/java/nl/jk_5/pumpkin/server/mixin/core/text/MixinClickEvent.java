package nl.jk_5.pumpkin.server.mixin.core.text;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.event.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.action.ClickAction;
import nl.jk_5.pumpkin.api.text.action.TextActions;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinClickEvent;

import java.net.MalformedURLException;
import java.net.URL;

@Mixin(ClickEvent.class)
public abstract class MixinClickEvent implements IMixinClickEvent {

    @Shadow private ClickEvent.Action action;
    @Shadow private String value;

    private ClickAction<?> handle;
    private boolean initialized;

    @Override
    public ClickAction<?> getHandle() {
        if (!this.initialized) {
            try {
                switch (this.action) {
                    case OPEN_URL:
                        try {
                            setHandle(TextActions.openUrl(new URL(this.value)));
                        } catch (MalformedURLException e) {
                            throw new IllegalArgumentException("Invalid URL: " + this.value, e);
                        }
                        break;
                    case RUN_COMMAND:
                        setHandle(TextActions.runCommand(this.value));
                        break;
                    case SUGGEST_COMMAND:
                        setHandle(TextActions.suggestCommand(this.value));
                        break;
                    case CHANGE_PAGE:
                        setHandle(TextActions.changePage(Integer.parseInt(this.value)));
                        break;
                    default:
                }
            } finally {
                this.initialized = true;
            }
        }

        return this.handle;
    }

    @Override
    public void setHandle(ClickAction<?> handle) {
        if (this.initialized) {
            return;
        }

        this.handle = checkNotNull(handle, "handle");
        this.initialized = true;
    }
}
