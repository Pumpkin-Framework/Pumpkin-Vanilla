package nl.jk_5.pumpkin.server.mixin.core.status;

import net.minecraft.network.ServerStatusResponse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.MinecraftVersion;
import nl.jk_5.pumpkin.api.ProtocolMinecraftVersion;
import nl.jk_5.pumpkin.server.util.PumpkinMinecraftVersion;

@Mixin(ServerStatusResponse.MinecraftProtocolVersionIdentifier.class)
public abstract class MixinMinecraftProtocolVersionIdentifier implements ProtocolMinecraftVersion {

    @Shadow
    private String name;

    @Shadow
    private int protocol;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    public boolean isLegacy() {
        return false;
    }

    @Override
    public int compareTo(MinecraftVersion o) {
        return PumpkinMinecraftVersion.compare(this, o);
    }
}
