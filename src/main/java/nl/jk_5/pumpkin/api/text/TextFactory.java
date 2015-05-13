package nl.jk_5.pumpkin.api.text;

import java.util.Locale;

/**
 * Represents the required implementation for the static methods in
 * {@link Texts}.
 */
public interface TextFactory {

    /**
     * Parses the specified JSON text and returns the parsed result.
     *
     * @param json The valid JSON text
     * @return The parsed text
     * @throws IllegalArgumentException If the JSON is invalid
     */
    Text parseJson(String json) throws IllegalArgumentException;

    /**
     * Parses the specified JSON text leniently and returns the parsed result.
     *
     * @param json The JSON text
     * @return The parsed text
     * @throws IllegalArgumentException If the JSON couldn't be parsed
     */
    Text parseJsonLenient(String json) throws IllegalArgumentException;

    /**
     * Returns a plain text representation of the {@link Text} without any
     * formattings.
     *
     * @param text The text to convert
     * @return The text converted to plain text
     */
    String toPlain(Text text);

    /**
     * Returns a plain text representation of the {@link Text} without any
     * formatting in the provided Locale.
     *
     * @param text The text to convert
     * @param locale The language to get the plain string in
     * @return The text converted to plain text
     */
    String toPlain(Text text, Locale locale);

    /**
     * Returns a JSON representation of the {@link Text} as used in commands.
     *
     * @param text The text to convert
     * @return The text converted to JSON
     */
    String toJson(Text text);

    /**
     * Returns a JSON representation of the {@link Text} as used in commands in the specified language.
     *
     * @param text The text to convert
     * @param locale The language to get the json in
     * @return The text converted to JSON
     */
    String toJson(Text text, Locale locale);

    /**
     * Returns the default legacy formatting character.
     *
     * @return The legacy formatting character
     */
    char getLegacyChar();

    /**
     * Creates a Message from a legacy string, given a color character.
     *
     * @param text The text to be converted as a String
     * @param code The color character to be replaced
     * @return The converted Message
     */
    Text.Literal parseLegacyMessage(String text, char code);

    /**
     * Removes the legacy formatting character from a legacy string.
     *
     * @param text The legacy text as a String
     * @param code The color character to be replaced
     * @return The stripped text
     */
    String stripLegacyCodes(String text, char code);

    /**
     * Replaces the given formatting character with another given formatting
     * character from a legacy string.
     *
     * @param text The legacy text as a String
     * @param from The color character to be replaced
     * @param to The color character to replace with
     * @return The replaced text
     */
    String replaceLegacyCodes(String text, char from, char to);

    /**
     * Returns a representation of the {@link Text} using the legacy color
     * codes.
     *
     * @param text The text to convert
     * @param code The legacy char to use for the message
     * @return The text converted to the old color codes
     */
    String toLegacy(Text text, char code);

    /**
     * Returns a representation of the {@link Text} using the legacy color
     * codes in the given Locale.
     *
     * @param text The text to convert
     * @param code The legacy char to use for the message
     * @param locale The language to translate into
     * @return The text converted to the old color codes
     */
    String toLegacy(Text text, char code, Locale locale);

}
