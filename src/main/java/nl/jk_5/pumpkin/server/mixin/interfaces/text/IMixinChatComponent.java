package nl.jk_5.pumpkin.server.mixin.interfaces.text;

import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.api.text.Text;

import java.util.Iterator;

public interface IMixinChatComponent extends IChatComponent {

    Iterator<IChatComponent> childrenIterator();

    Iterable<IChatComponent> withChildren();

    String toPlain();

    String getLegacyFormatting();

    String toLegacy(char code);

    Text toText();

}
