package nl.jk_5.pumpkin.api.text.translation;

import java.util.Locale;

/**
 * Represents an identifier for text that can be translated into multiple
 * languages. Minecraft-included translations are generally translated clientside.
 * Translations not included in Minecraft are generally expected to
 * be translated server-side, for example using Gettext or a
 * {@link java.util.ResourceBundle}
 *
 * <p>Some translations require parameters to be sent together with them, if
 * they're not given they will be filled with empty text.</p>
 *
 * <p>While the client has multiple locales available, server-side vanilla translations
 * support only {@link Locale#ENGLISH}.</p>
 */
public interface Translation {

    /**
     * Returns identifier for this {@link Translation}.
     *
     * @return The translation identifier of this translation
     */
    String getId();

    /**
     * Gets the default translation without any parameters replaced.
     *
     * @param locale The language to get the translated format string for
     * @return The default translation without any parameters
     */
    String get(Locale locale);

    /**
     * Gets the default translation format with the specified parameters.
     *
     * @param locale The language to get the translated string for
     * @param args The parameters for this translation
     * @return The default translation with the specified parameters
     */
    String get(Locale locale, Object... args);
}
