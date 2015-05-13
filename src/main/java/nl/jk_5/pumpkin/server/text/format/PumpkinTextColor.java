package nl.jk_5.pumpkin.server.text.format;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.util.EnumChatFormatting;

import nl.jk_5.pumpkin.api.text.format.TextColor;
import nl.jk_5.pumpkin.server.registry.PumpkinGameRegistry;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.awt.*;

@NonnullByDefault
public class PumpkinTextColor implements TextColor.Base {

    private final EnumChatFormatting handle;
    private final Color color;

    @Override
    public String getId() {
        return this.handle.name();
    }

    public PumpkinTextColor(EnumChatFormatting handle, Color color) {
        this.handle = checkNotNull(handle, "handle");
        this.color = checkNotNull(color, "color");
    }

    public EnumChatFormatting getHandle() {
        return this.handle;
    }

    @Override
    public String getName() {
        return this.handle.getFriendlyName();
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    @Deprecated
    public char getCode() {
        return this.handle.formattingCode;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static PumpkinTextColor of(EnumChatFormatting color) {
        return PumpkinGameRegistry.enumChatColor.get(color);
    }

}
