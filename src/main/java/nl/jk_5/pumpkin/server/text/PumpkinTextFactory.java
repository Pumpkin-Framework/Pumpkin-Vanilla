package nl.jk_5.pumpkin.server.text;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static nl.jk_5.pumpkin.server.text.PumpkinTexts.COLOR_CHAR;
import static nl.jk_5.pumpkin.server.text.PumpkinTexts.getDefaultLocale;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.TextBuilder;
import nl.jk_5.pumpkin.api.text.TextFactory;
import nl.jk_5.pumpkin.api.text.Texts;
import nl.jk_5.pumpkin.api.text.format.TextColors;
import nl.jk_5.pumpkin.api.text.format.TextStyles;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatComponent;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinText;
import nl.jk_5.pumpkin.server.text.format.PumpkinTextColor;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NonnullByDefault
public class PumpkinTextFactory implements TextFactory {

    @Override
    public Text parseJson(String json) throws IllegalArgumentException {
        try {
            return ((IMixinChatComponent) IChatComponent.Serializer.jsonToComponent(json)).toText();
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse JSON", e);
        }
    }

    @Override
    public Text parseJsonLenient(String json) throws IllegalArgumentException {
        return parseJson(json); // TODO
    }

    @Override
    public String toPlain(Text text) {
        return toPlain(text, getDefaultLocale());
    }

    @Override
    public String toPlain(Text text, Locale locale) {
        return ((IMixinText) text).toPlain(locale);
    }

    @Override
    public String toJson(Text text) {
        return toJson(text, getDefaultLocale());
    }

    @Override
    public String toJson(Text text, Locale locale) {
        return ((IMixinText) text).toJson(locale);
    }

    @Override
    public char getLegacyChar() {
        return COLOR_CHAR;
    }

    private static final ImmutableMap<Character, EnumChatFormatting> CHAR_TO_FORMATTING;

    static {
        ImmutableMap.Builder<Character, EnumChatFormatting> builder = ImmutableMap.builder();

        for (EnumChatFormatting formatting : EnumChatFormatting.values()) {
            builder.put(formatting.formattingCode, formatting);
        }

        CHAR_TO_FORMATTING = builder.build();
    }

    private static void applyStyle(TextBuilder builder, char code) {
        EnumChatFormatting formatting = CHAR_TO_FORMATTING.get(code);
        if (formatting != null) {
            switch (formatting) {
                case BOLD:
                    builder.style(TextStyles.BOLD);
                    break;
                case ITALIC:
                    builder.style(TextStyles.ITALIC);
                    break;
                case UNDERLINE:
                    builder.style(TextStyles.UNDERLINE);
                    break;
                case STRIKETHROUGH:
                    builder.style(TextStyles.STRIKETHROUGH);
                    break;
                case OBFUSCATED:
                    builder.style(TextStyles.OBFUSCATED);
                    break;
                case RESET:
                    builder.color(TextColors.NONE);
                    builder.style(TextStyles.RESET);
                    break;
                default:
                    builder.color(PumpkinTextColor.of(formatting));
            }
        }
    }

    private static Text.Literal parseLegacyMessage(String text, int pos, Matcher matcher, TextBuilder.Literal parent) {
        String content = text.substring(pos, matcher.start());
        parent.content(content);

        TextBuilder.Literal builder = Texts.builder("");
        applyStyle(builder, matcher.group(1).charAt(0));

        int end = matcher.end();
        while (true) {
            if (!matcher.find()) {
                builder.content(text.substring(end));
                return builder.build();
            } else if (end == matcher.start()) {
                applyStyle(builder, matcher.group(1).charAt(0));
                end = matcher.end();
            } else {
                break;
            }
        }

        builder.append(parseLegacyMessage(text, end, matcher, builder));
        return builder.build();
    }

    @Override
    public Text.Literal parseLegacyMessage(String text, char code) {
        if (text.length() <= 1) {
            return Texts.of(text);
        }

        Matcher matcher = (code == COLOR_CHAR ? FORMATTING_PATTERN :
                Pattern.compile(code + "([0-9A-FK-OR])", CASE_INSENSITIVE)).matcher(text);
        if (!matcher.find()) {
            return Texts.of(text);
        }

        TextBuilder.Literal builder = Texts.builder("");
        return builder.append(parseLegacyMessage(text, 0, matcher, builder)).build();
    }

    private static final Pattern FORMATTING_PATTERN = Pattern.compile(COLOR_CHAR + "([0-9A-FK-OR])", CASE_INSENSITIVE);

    @Override
    public String stripLegacyCodes(String text, char code) {
        if (code == COLOR_CHAR) {
            return FORMATTING_PATTERN.matcher(text).replaceAll("");
        }

        return text.replaceAll("(?i)" + code + "[0-9A-FK-OR]", "");
    }

    @Override
    public String replaceLegacyCodes(String text, char from, char to) {
        if (from == COLOR_CHAR) {
            return FORMATTING_PATTERN.matcher(text).replaceAll(to + "$1");
        }

        return text.replaceAll("(?i)" + from + "([0-9A-FK-OR])", to + "$1");
    }

    @Override
    public String toLegacy(Text text, char code) {
        return toLegacy(text, code, getDefaultLocale());
    }

    @Override
    public String toLegacy(Text text, char code, Locale locale) {
        return ((IMixinText) text).toLegacy(code, locale);
    }

}
