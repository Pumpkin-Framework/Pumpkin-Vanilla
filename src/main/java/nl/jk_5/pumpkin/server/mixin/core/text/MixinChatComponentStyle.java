package nl.jk_5.pumpkin.server.mixin.core.text;

import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.TextBuilder;
import nl.jk_5.pumpkin.api.text.action.TextActions;
import nl.jk_5.pumpkin.api.text.format.TextStyle;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatComponent;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatStyle;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinClickEvent;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinHoverEvent;
import nl.jk_5.pumpkin.server.text.ChatComponentIterable;
import nl.jk_5.pumpkin.server.text.PumpkinTexts;
import nl.jk_5.pumpkin.server.text.format.PumpkinTextColor;

import java.util.Iterator;
import java.util.List;

@Mixin(ChatComponentStyle.class)
public abstract class MixinChatComponentStyle implements IMixinChatComponent {

    @Shadow private ChatStyle style;
    @Shadow protected List<IChatComponent> siblings;

    private char[] formatting;

    protected TextBuilder createBuilder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<IChatComponent> childrenIterator() {
        return getSiblings().iterator();
    }

    @Override
    public Iterable<IChatComponent> withChildren() {
        return new ChatComponentIterable(this, true);
    }

    private char[] getFormatting() {
        if (this.formatting == null) {
            this.formatting = ((IMixinChatStyle) this.style).asFormattingCode();
        }

        return this.formatting;
    }

    @Override
    public String toPlain() {
        StringBuilder builder = new StringBuilder();

        for (IChatComponent component : withChildren()) {
            builder.append(component.getUnformattedTextForChat());
        }

        return builder.toString();
    }

    @Override
    public String getLegacyFormatting() {
        char[] formatting = getFormatting();
        char[] buf = new char[formatting.length * 2];

        for (int i = 0; i < formatting.length; i++) {
            buf[i * 2] = PumpkinTexts.COLOR_CHAR;
            buf[i * 2 + 1] = formatting[i];
        }

        return new String(buf);
    }

    @Override
    public String toLegacy(char code) {
        StringBuilder builder = new StringBuilder();

        for (IChatComponent component : withChildren()) {
            char[] formatting = ((MixinChatComponentStyle) component).getFormatting();
            builder.ensureCapacity(formatting.length * 2 + 16);
            for (char formattingCode : formatting) {
                builder.append(code).append(formattingCode);
            }

            builder.append(component.getUnformattedTextForChat());
            builder.append(code).append(EnumChatFormatting.RESET.formattingCode);
        }

        return builder.toString();
    }

    @Override
    public Text toText() {
        TextBuilder builder = createBuilder();

        if (this.style != null) {
            if (this.style.color != null) {
                builder.color(PumpkinTextColor.of(this.style.color));
            }

            builder.style(new TextStyle(this.style.bold, this.style.italic, this.style.underlined, this.style.strikethrough, this.style.obfuscated));

            if (this.style.chatClickEvent != null) {
                builder.onClick(((IMixinClickEvent) this.style.chatClickEvent).getHandle());
            }
            if (this.style.chatHoverEvent != null) {
                builder.onHover(((IMixinHoverEvent) this.style.chatHoverEvent).getHandle());
            }
            if (this.style.insertion != null) {
                builder.onShiftClick(TextActions.insertText(this.style.insertion));
            }
        }

        for (IChatComponent child : this.siblings) {
            builder.append(((IMixinChatComponent) child).toText());
        }

        return builder.build();
    }

}
