package nl.jk_5.pumpkin.server.text;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatComponent;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

public class ChatComponentIterator extends UnmodifiableIterator<IChatComponent> {

    private IMixinChatComponent component;
    private Iterator<IChatComponent> children;
    @Nullable private Iterator<IChatComponent> currentChildIterator;

    public ChatComponentIterator(IMixinChatComponent component) {
        this.component = checkNotNull(component, "component");
    }

    public ChatComponentIterator(Iterator<IChatComponent> children) {
        this.children = checkNotNull(children, "children");
    }

    @Override
    public boolean hasNext() {
        return this.component != null || (this.currentChildIterator != null && this.currentChildIterator.hasNext()) || this.children.hasNext();
    }

    @Override
    @SuppressWarnings("unchecked")
    public IChatComponent next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (this.component != null) {
            this.children = this.component.childrenIterator();

            IChatComponent result = this.component;
            this.component = null;
            return result;
        } else if (this.currentChildIterator == null || !this.currentChildIterator.hasNext()) {
            this.currentChildIterator = ((IMixinChatComponent) this.children.next()).withChildren().iterator();
        }

        return this.currentChildIterator.next();
    }

}
