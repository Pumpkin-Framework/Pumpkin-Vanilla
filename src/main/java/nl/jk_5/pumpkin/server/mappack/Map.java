package nl.jk_5.pumpkin.server.mappack;

import net.minecraft.stats.StatisticsFile;

import nl.jk_5.pumpkin.api.mappack.Mappack;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.lua.*;
import nl.jk_5.pumpkin.server.lua.map.MachineImpl;
import nl.jk_5.pumpkin.server.permissions.MapPermissionsHandler;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

@NonnullByDefault
public class Map implements MachineHost, CallbackContainer {

    private final Mappack mappack;
    private final File dir;
    private final String internalName;
    private final MachineImpl machine;

    private final MapPermissionsHandler permissionsHandler = new MapPermissionsHandler(this);

    private final java.util.Map<UUID, StatisticsFile> playerStats = new HashMap<UUID, StatisticsFile>();

    private boolean firstTick = true;

    private MapWorld defaultWorld;

    public Map(Mappack mappack, File dir) {
        this.mappack = mappack;
        this.dir = dir;
        this.internalName = dir.getName();

        this.machine = new MachineImpl(this);
    }

    public void tick() {
        if(firstTick){
            machine.start();
            firstTick = false;
        }
        machine.update();
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

    @Override
    public Machine getMachine() {
        return machine;
    }

    @Override
    public String getScript() {
        return "local id = map.mappack().id()\nprint(tostring(id))";
    }

    @Callback(doc = "function():Mappack -- Returns the mappack loaded in this map.")
    public Object[] mappack(Context context, Arguments args){
        return new Object[]{mappack};
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
