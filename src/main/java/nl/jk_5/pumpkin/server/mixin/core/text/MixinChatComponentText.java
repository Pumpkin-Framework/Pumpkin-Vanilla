package nl.jk_5.pumpkin.server.mixin.core.text;

import net.minecraft.util.ChatComponentText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.TextBuilder;
import nl.jk_5.pumpkin.api.text.Texts;

@Mixin(ChatComponentText.class)
public abstract class MixinChatComponentText extends MixinChatComponentStyle {

    @Shadow private String text;

    @Override
    protected TextBuilder createBuilder() {
        return Texts.builder(this.text);
    }

}
