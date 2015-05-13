package nl.jk_5.pumpkin.server.mixin.core.text;

import net.minecraft.util.ChatComponentSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.TextBuilder;
import nl.jk_5.pumpkin.api.text.Texts;
import nl.jk_5.pumpkin.api.text.selector.Selectors;

@Mixin(ChatComponentSelector.class)
public abstract class MixinChatComponentSelector extends MixinChatComponentStyle {

    @Shadow private String selector;

    @Override
    protected TextBuilder createBuilder() {
        return Texts.builder(Selectors.parse(this.selector));
    }

}
