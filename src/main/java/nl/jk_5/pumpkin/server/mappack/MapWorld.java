package nl.jk_5.pumpkin.server.mappack;

import net.minecraft.world.WorldServer;

import nl.jk_5.pumpkin.api.mappack.MappackWorld;
import nl.jk_5.pumpkin.api.mappack.WorldContext;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public class MapWorld {

    private final WorldServer wrapped;
    private final MappackWorld config;

    @Nullable
    private Map map;

    public MapWorld(WorldServer wrapped, WorldContext context) {
        this.wrapped = wrapped;
        this.config = context.getConfig();
    }

    public WorldServer getWrapped() {
        return wrapped;
    }

    @Nullable
    public Map getMap() {
        return map;
    }

    public void setMap(@Nullable Map map) {
        this.map = map;
    }

    public MappackWorld getConfig() {
        return config;
    }

    public void onPlayerJoined(Player player) {

    }

    public void onPlayerLeft(Player player) {

    }
}
