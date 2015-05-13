package nl.jk_5.pumpkin.server.mixin.api.text;

import com.google.common.base.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.scoreboard.Score;
import nl.jk_5.pumpkin.api.text.Text;

@Mixin(value = Text.Score.class, remap = false)
public abstract class MixinTextScore extends MixinText {

    @Shadow protected Score score;
    @Shadow protected Optional<String> override;

    /*@Override
    protected ChatComponentStyle createComponent(Locale locale) {
        ChatComponentScore component = new ChatComponentScore(null, null); // TODO
        if (this.override.isPresent()) {
            component.setValue(this.override.get());
        }
        return component;
    }*/
}
