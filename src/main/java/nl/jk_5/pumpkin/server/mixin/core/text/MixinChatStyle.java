package nl.jk_5.pumpkin.server.mixin.core.text;

import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatStyle;

import java.util.Arrays;

@Mixin(ChatStyle.class)
public abstract class MixinChatStyle implements IMixinChatStyle {

    @Shadow private ChatStyle parentStyle;

    @Shadow
    public abstract EnumChatFormatting getColor();

    @Shadow
    public abstract boolean getBold();

    @Shadow
    public abstract boolean getItalic();

    @Shadow
    public abstract boolean getStrikethrough();

    @Shadow
    public abstract boolean getUnderlined();

    @Shadow
    public abstract boolean getObfuscated();

    @Shadow
    public abstract boolean isEmpty();

    @Override
    public char[] asFormattingCode() {
        if (this.isEmpty()) {
            return this.parentStyle != null ? ((IMixinChatStyle) this.parentStyle).asFormattingCode() : ArrayUtils.EMPTY_CHAR_ARRAY;
        } else {
            char[] buf = new char[6];
            int i = 0;

            EnumChatFormatting color = getColor();
            if (color != null) {
                buf[i++] = color.formattingCode;
            }

            if (getBold()) {
                buf[i++] = EnumChatFormatting.BOLD.formattingCode;
            }

            if (getItalic()) {
                buf[i++] = EnumChatFormatting.ITALIC.formattingCode;
            }

            if (getUnderlined()) {
                buf[i++] = EnumChatFormatting.UNDERLINE.formattingCode;
            }

            if (getObfuscated()) {
                buf[i++] = EnumChatFormatting.STRIKETHROUGH.formattingCode;
            }

            if (getStrikethrough()) {
                buf[i++] = EnumChatFormatting.STRIKETHROUGH.formattingCode;
            }

            return i == buf.length ? buf : Arrays.copyOf(buf, i);
        }
    }
}
