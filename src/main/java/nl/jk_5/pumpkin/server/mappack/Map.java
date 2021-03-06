package nl.jk_5.pumpkin.server.mappack;

import net.minecraft.stats.StatisticsFile;

import nl.jk_5.pumpkin.api.mappack.Mappack;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.permissions.MapPermissionsHandler;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

@NonnullByDefault
public class Map {

    private final Mappack mappack;
    private final File dir;
    private final String internalName;

    private final MapPermissionsHandler permissionsHandler = new MapPermissionsHandler(this);

    private final java.util.Map<UUID, StatisticsFile> playerStats = new HashMap<UUID, StatisticsFile>();

    private MapWorld defaultWorld;

    public Map(Mappack mappack, File dir) {
        this.mappack = mappack;
        this.dir = dir;
        this.internalName = dir.getName();
    }

    public MapWorld getPrimaryWorld(){
        return this.defaultWorld;
    }

    public Collection<MapWorld> getWorlds(){
        return Pumpkin.instance().getMapLoader().getWorlds(this);
    }

    public void addWorld(MapWorld world) {
        Pumpkin.instance().getMapLoader().addWorldToMap(world, this);
        if(world.getConfig().isDefault()){
            defaultWorld = world;
        }
    }

    public void onPlayerJoined(Player player) {

    }

    public void onPlayerLeft(Player player) {

    }



    public String getInternalName() {
        return internalName;
    }

    public Mappack getMappack() {
        return mappack;
    }

    public MapPermissionsHandler getPermissionsHandler() {
        return permissionsHandler;
    }

    public java.util.Map<UUID, StatisticsFile> getPlayerStats() {
        return playerStats;
    }

    public File getDir() {
        return dir;
    }
}
