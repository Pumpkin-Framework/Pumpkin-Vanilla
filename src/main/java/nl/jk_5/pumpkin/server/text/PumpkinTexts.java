package nl.jk_5.pumpkin.server.text;

import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.TextBuilder;
import nl.jk_5.pumpkin.api.text.Texts;
import nl.jk_5.pumpkin.api.text.format.TextColors;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatComponent;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinText;

import java.util.Locale;

public final class PumpkinTexts {

    public static final char COLOR_CHAR = '\u00A7';

    private PumpkinTexts() {
    }

    public static Locale getDefaultLocale() { // TODO: Get this from the MC client?
        return Locale.getDefault();
    }

    public static IChatComponent toComponent(Text text) {
        return toComponent(text, getDefaultLocale());
    }

    public static IChatComponent toComponent(Text text, Locale locale) {
        return ((IMixinText) text).toComponent(locale);
    }

    public static Text toText(IChatComponent component) {
        return ((IMixinChatComponent) component).toText();
    }

    public static String toPlain(IChatComponent component) {
        return ((IMixinChatComponent) component).toPlain();
    }

    public static String toLegacy(IChatComponent component) {
        return toLegacy(component, COLOR_CHAR);
    }

    public static String toLegacy(IChatComponent component, char code) {
        return ((IMixinChatComponent) component).toLegacy(code);
    }

    private static String getLegacyFormatting(Text text) {
        return ((IMixinText) text).getLegacyFormatting();
    }

    public static Text fixActionBarFormatting(Text text) {
        Text result = text;
        if (!text.getChildren().isEmpty()) {
            TextBuilder fixed = text.builder().removeAll();

            for (Text child : text.getChildren()) {
                fixed.append(fixActionBarFormatting(child));
            }

            result = fixed.build();
        }

        if (text.getColor() != TextColors.NONE || !text.getStyle().isEmpty()) {
            result = Texts.builder(getLegacyFormatting(text)).append(result).build();
        }

        return result;
    }

}
