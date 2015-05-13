package nl.jk_5.pumpkin.server.text;

import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatComponent;

import java.util.Iterator;

public class ChatComponentIterable implements Iterable<IChatComponent> {

    private final IMixinChatComponent component;
    private final boolean includeSelf;

    public ChatComponentIterable(IMixinChatComponent component, boolean includeSelf) {
        this.component = component;
        this.includeSelf = includeSelf;
    }

    @Override
    public Iterator<IChatComponent> iterator() {
        if (this.includeSelf) {
            return new ChatComponentIterator(this.component);
        } else {
            return new ChatComponentIterator(this.component.childrenIterator());
        }
    }

}
