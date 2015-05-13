package nl.jk_5.pumpkin.server.text.format;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.util.EnumChatFormatting;

import nl.jk_5.pumpkin.api.text.format.TextStyle;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public class PumpkinTextStyle extends TextStyle.Base {

    private final EnumChatFormatting handle;

    PumpkinTextStyle(EnumChatFormatting handle, @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underline, @Nullable Boolean strikethrough, @Nullable Boolean obfuscated) {
        super(bold, italic, underline, strikethrough, obfuscated);
        this.handle = checkNotNull(handle, "handle");
    }

    @Override
    public String getName() {
        return this.handle.name();
    }

    @Override
    @Deprecated
    public char getCode() {
        return this.handle.formattingCode;
    }

    public static PumpkinTextStyle of(EnumChatFormatting handle) {
        if (handle == EnumChatFormatting.RESET) {
            return new PumpkinTextStyle(handle, false, false, false, false, false);
        }

        return new PumpkinTextStyle(handle,
                equalsOrNull(handle, EnumChatFormatting.BOLD),
                equalsOrNull(handle, EnumChatFormatting.ITALIC),
                equalsOrNull(handle, EnumChatFormatting.UNDERLINE),
                equalsOrNull(handle, EnumChatFormatting.STRIKETHROUGH),
                equalsOrNull(handle, EnumChatFormatting.OBFUSCATED)
        );
    }

    @Nullable
    private static Boolean equalsOrNull(EnumChatFormatting handle, EnumChatFormatting check) {
        return handle == check ? true : null;
    }

}
