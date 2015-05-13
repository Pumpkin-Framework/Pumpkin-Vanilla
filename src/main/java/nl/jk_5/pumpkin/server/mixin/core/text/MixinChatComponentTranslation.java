package nl.jk_5.pumpkin.server.mixin.core.text;

import com.google.common.collect.Iterators;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.TextBuilder;
import nl.jk_5.pumpkin.api.text.Texts;
import nl.jk_5.pumpkin.api.text.translation.Translation;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatComponent;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatComponentTranslation;
import nl.jk_5.pumpkin.server.text.ChatComponentIterable;
import nl.jk_5.pumpkin.server.text.translation.PumpkinTranslation;

import java.util.Iterator;
import java.util.List;

@Mixin(ChatComponentTranslation.class)
public abstract class MixinChatComponentTranslation extends MixinChatComponentStyle implements IMixinChatComponentTranslation {

    @Shadow private String key;
    @Shadow private Object[] formatArgs;
    private Translation translation;

    @Override
    public void setTranslation(Translation translation) {
        this.translation = translation;
        if (translation instanceof PumpkinTranslation) {
            this.key = translation.getId();
        }
    }

    @Shadow List<IChatComponent> children;
    @Shadow abstract void ensureInitialized();

    @Override
    protected TextBuilder createBuilder() {
        if (this.translation == null) {
            this.translation = new PumpkinTranslation(this.key);
        }
        return Texts.builder(this.translation, wrapFormatArgs(this.formatArgs));
    }

    @Override
    public Iterator<IChatComponent> childrenIterator() {
        ensureInitialized();
        return Iterators.concat(this.children.iterator(), super.childrenIterator());
    }

    @Override
    public Iterable<IChatComponent> withChildren() {
        return new ChatComponentIterable(this, false);
    }

    private static Object[] wrapFormatArgs(Object... formatArgs) {
        Object[] ret = new Object[formatArgs.length];
        for (int i = 0; i < formatArgs.length; ++i) {
            if (formatArgs[i] instanceof IMixinChatComponent) {
                ret[i] = ((IMixinChatComponent) formatArgs[i]).toText();
            } else {
                ret[i] = formatArgs[i];
            }
        }
        return ret;
    }
}
