package nl.jk_5.pumpkin.server.mixin.core.text;

import net.minecraft.util.ChatStyle;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;

import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatStyle;

@Mixin(targets = "net/minecraft/util/ChatStyle$1")
public abstract class MixinChatStyleRoot extends ChatStyle implements IMixinChatStyle {

    @Override
    public char[] asFormattingCode() {
        return ArrayUtils.EMPTY_CHAR_ARRAY;
    }

}
