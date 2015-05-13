package nl.jk_5.pumpkin.server.mixin.api.text;

import net.minecraft.util.ChatComponentSelector;
import net.minecraft.util.ChatComponentStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.selector.Selector;

import java.util.Locale;

@Mixin(value = Text.Selector.class, remap = false)
public abstract class MixinTextSelector extends MixinText {

    @Shadow protected Selector selector;

    @Override
    protected ChatComponentStyle createComponent(Locale locale) {
        return new ChatComponentSelector(this.selector.toPlain());
    }
}
