package nl.jk_5.pumpkin.server.mixin;

import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.server.permission.api.PlayerIdentity;

import java.util.UUID;

@Mixin(value = GameProfile.class, remap = false)
@Implements(@Interface(iface = PlayerIdentity.class, prefix = "ident$"))
public abstract class MixinGameProfile {

    @Shadow
    public abstract UUID getId();

    @Shadow
    public abstract String getName();

    public UUID ident$getId() {
        return this.getId();
    }

    public String ident$getUsername() {
        return this.getName();
    }
}
