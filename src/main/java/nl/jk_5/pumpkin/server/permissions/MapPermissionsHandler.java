package nl.jk_5.pumpkin.server.permissions;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.api.mappack.Mappack;
import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.permissions.zone.*;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseMappack;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseZone;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.*;
import javax.annotation.Nullable;

@NonnullByDefault
public class MapPermissionsHandler {

    private static final Logger logger = LogManager.getLogger();

    private final Map map;

    /**
     * Order is very important in this list.
     * Starts by the most specific, highest priority zone
     * Ends with least specific, lowest priority zone (root zone with default values)
     */
    private List<Zone> zones = Collections.emptyList();

    public MapPermissionsHandler(Map map) {
        this.map = map;
    }

    public void load() {
        Mappack mappack = map.getMappack();

        List<Zone> builder = new ArrayList<Zone>();

        for (DatabaseZone zone : ((DatabaseMappack) mappack).getZones()) {
            if(zone.getType().equals("map")){
                builder.add(new MapZone(zone));
            }else if(zone.getType().equals("world")){
                builder.add(new WorldZone(zone));
            }else if(zone.getType().equals("team")){
                builder.add(new TeamZone(zone));
            }else if(zone.getType().equals("area")){
                builder.add(new AreaZone(zone));
            }
        }
        Collections.sort(builder, new ZoneTypeComparator());

        this.zones = ImmutableList.copyOf(builder);
    }

    @Nullable
    public String getPermission(String owner, String permission) {
        User user = null;
        Player player = null;

        if(owner.startsWith("user:")){
            user = Pumpkin.instance().getUserManager().getById(Integer.parseInt(owner.substring(5)));
            for (Player p : Pumpkin.instance().getPlayerManager().getOnlinePlayers()) {
                if(p.getUser() == user){
                    player = p;
                }
            }
        }else if(owner.startsWith("player:")){
            player = Pumpkin.instance().getPlayerManager().getById(UUID.fromString(owner.substring(7)));
            assert player != null;
            user = player.getUser();
        }

        for (Zone zone : zones) {
            if (zone.isInZone(player, user)) {
                String value = zone.getPermissionValue(owner, permission);
                if(value != null){
                    return value;
                }
            }
        }

        return null;
    }

    private static class ZoneTypeComparator implements Comparator<Zone> {

        @Override
        public int compare(Zone z1, Zone z2) {
            if(z1 instanceof MapZone && z2 instanceof MapZone){
                return 0;
            }else if(z1 instanceof WorldZone && z2 instanceof WorldZone){
                return 0;
            }else if(z1 instanceof TeamZone && z2 instanceof TeamZone){
                return 0;
            }else if(z1 instanceof AreaZone && z2 instanceof AreaZone){
                return 0;
            }else if(z1 instanceof MapZone){
                return 1;
            }else if(z2 instanceof MapZone){
                return -1;
            }else if(z1 instanceof TeamZone && z2 instanceof WorldZone){
                return -1;
            }else if(z1 instanceof WorldZone && z2 instanceof TeamZone){
                return 1;
            }else if(z1 instanceof AreaZone){
                return -1;
            }else if(z2 instanceof AreaZone){
                return 1;
            }
            return 0;
        }
    }
}
