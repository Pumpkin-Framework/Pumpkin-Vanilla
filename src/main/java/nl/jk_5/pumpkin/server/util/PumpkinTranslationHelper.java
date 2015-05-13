package nl.jk_5.pumpkin.server.util;

import com.google.common.base.Function;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.Texts;
import nl.jk_5.pumpkin.api.text.translation.ResourceBundleTranslation;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.Nullable;

/**
 * This class provides translations for Pumpkin messages.
 */
public final class PumpkinTranslationHelper {

    private static final Function<Locale, ResourceBundle> LOOKUP_FUNC = new Function<Locale, ResourceBundle>() {
        @Nullable
        @Override
        public ResourceBundle apply(Locale input) {
            return ResourceBundle.getBundle("nl.jk_5.pumpkin.Translations", input);
        }
    };

    private PumpkinTranslationHelper() {
    }

    /**
     * Get the translated text for a given string.
     *
     * @param key The translation key
     * @param args Translation parameters
     * @return The translatable text
     */
    public static Text t(String key, Object... args) {
        return Texts.of(new ResourceBundleTranslation(key, LOOKUP_FUNC), args);
    }
}
