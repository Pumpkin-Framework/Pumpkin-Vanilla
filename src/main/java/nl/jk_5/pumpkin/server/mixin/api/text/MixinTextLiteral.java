package nl.jk_5.pumpkin.server.mixin.api.text;

import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.Text;

import java.util.Locale;

@Mixin(value = Text.Literal.class, remap = false)
public abstract class MixinTextLiteral extends MixinText {

    @Shadow protected String content;

    @Override
    protected ChatComponentStyle createComponent(Locale locale) {
        return new ChatComponentText(this.content);
    }

}
