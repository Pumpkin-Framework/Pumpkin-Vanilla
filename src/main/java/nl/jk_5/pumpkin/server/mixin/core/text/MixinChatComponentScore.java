package nl.jk_5.pumpkin.server.mixin.core.text;

import net.minecraft.util.ChatComponentScore;
import org.spongepowered.asm.mixin.Mixin;

import nl.jk_5.pumpkin.api.text.TextBuilder;
import nl.jk_5.pumpkin.api.text.Texts;

@Mixin(ChatComponentScore.class)
public abstract class MixinChatComponentScore extends MixinChatComponentStyle {

    @Override
    protected TextBuilder createBuilder() {
        return Texts.builder();
    }

}
