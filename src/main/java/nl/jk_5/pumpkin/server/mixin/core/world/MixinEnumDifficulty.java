package nl.jk_5.pumpkin.server.mixin.core.world;

import net.minecraft.world.EnumDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.world.difficulty.Difficulty;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
@Mixin(EnumDifficulty.class)
public class MixinEnumDifficulty implements Difficulty {

    @Shadow
    private int difficultyId;

    @Shadow
    private String difficultyResourceKey;

    @Override
    public String getId() {
        return this.difficultyResourceKey;
    }

    @Override
    public String getName() {
        return this.difficultyResourceKey.replace("options.difficulty.", "");
    }
}
