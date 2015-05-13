package nl.jk_5.pumpkin.server.mixin.core.status;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.ServerStatusResponse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.event.server.StatusPingEvent;

import java.util.Collections;
import java.util.List;

@Mixin(ServerStatusResponse.PlayerCountData.class)
public abstract class MixinPlayerCountData implements StatusPingEvent.Response.Players {

    private List<GameProfile> profiles;

    @Shadow
    private int onlinePlayerCount;

    @Shadow
    private int maxPlayers;

    @Override
    public int getOnline() {
        return this.onlinePlayerCount;
    }

    @Override
    public void setOnline(int online) {
        this.onlinePlayerCount = online;
    }

    @Override
    public int getMax() {
        return this.maxPlayers;
    }

    @Override
    public void setMax(int max) {
        this.maxPlayers = max;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GameProfile> getProfiles() {
        if (this.profiles == null) {
            this.profiles = Lists.newArrayList();
        }

        return (List) this.profiles;
    }

    @Overwrite
    public GameProfile[] getPlayers() {
        if (this.profiles == null) {
            this.profiles = Lists.newArrayList();
        }

        // TODO: When serializing, Minecraft calls this method frequently (it doesn't store the result).
        // Maybe we should cache this until the list is modified or patch the serialization?
        return this.profiles.toArray(new GameProfile[this.profiles.size()]);
    }

    @Overwrite
    public void setPlayers(GameProfile[] playersIn) {
        if (this.profiles == null) {
            this.profiles = Lists.newArrayList(playersIn);
        } else {
            this.profiles.clear();
            Collections.addAll(this.profiles, playersIn);
        }
    }
}
