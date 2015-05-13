package nl.jk_5.pumpkin.server.mixin.interfaces.text;

import net.minecraft.util.IChatComponent;

import java.util.Locale;

public interface IMixinText {

    IChatComponent toComponent(Locale locale);

    String toPlain(Locale locale);

    String toJson(Locale locale);

    String getLegacyFormatting();

    String toLegacy(char code, Locale locale);

}
