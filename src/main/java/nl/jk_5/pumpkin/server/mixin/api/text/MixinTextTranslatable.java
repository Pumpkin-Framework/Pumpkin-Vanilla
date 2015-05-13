package nl.jk_5.pumpkin.server.mixin.api.text;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentTranslation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.translation.Translation;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatComponentTranslation;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinText;
import nl.jk_5.pumpkin.server.text.translation.PumpkinTranslation;

import java.util.Locale;

@Mixin(value = Text.Translatable.class, remap = false)
public abstract class MixinTextTranslatable extends MixinText {

    @Shadow protected Translation translation;
    @Shadow protected ImmutableList<Object> arguments;

    @Override
    protected ChatComponentStyle createComponent(Locale locale) {
        ChatComponentTranslation ret = new ChatComponentTranslation(this.translation instanceof PumpkinTranslation ? this.translation.getId() : this.translation.get(locale), unwrapArguments(this.arguments, locale));
        ((IMixinChatComponentTranslation) ret).setTranslation(this.translation);
        return ret;
    }

    private Object[] unwrapArguments(ImmutableList<Object> args, Locale locale) {
        Object[] ret = new Object[args.size()];
        for (int i = 0; i < args.size(); ++i) {
            final Object arg = args.get(i);
            if (arg instanceof IMixinText) {
                ret[i] = ((IMixinText) arg).toComponent(locale);
            } else {
                ret[i] = arg;
            }
        }
        return ret;
    }

}
