package nl.jk_5.pumpkin.server.permission.impl.zone;

import net.minecraft.entity.player.EntityPlayer;

import nl.jk_5.pumpkin.server.permission.api.PermissionList;
import nl.jk_5.pumpkin.server.permission.api.PlayerIdentity;
import nl.jk_5.pumpkin.server.permission.api.Zone;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;
import nl.jk_5.pumpkin.server.util.interfaces.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@NonnullByDefault
abstract class AbstractZone implements Zone {

    private static final String PERMISSION_ASTERIX = "*";
    private static final String PERMISSION_FALSE = "false";
    private static final String PERMISSION_TRUE = "true";
    private static final String ALL_PERMS = "." + PERMISSION_ASTERIX;

    protected Map<PlayerIdentity, PermissionList> playerPermissions = new HashMap<PlayerIdentity, PermissionList>();
    protected Map<String, PermissionList> groupPermissions = new HashMap<String, PermissionList>();

    @Deprecated
    public int getId(){
        return -1;
    }

    public boolean isPlayerInZone(EntityPlayer player) {
        return isInZone(((PlayerEntity) player).getLocation());
    }

    @Override
    public Map<PlayerIdentity, PermissionList> getAllPlayerPermissions() {
        return this.playerPermissions;
    }

    @Override
    @Nullable
    public PermissionList getPlayerPermissions(PlayerIdentity player) {
        return this.playerPermissions.get(player);
    }

    @Override
    @Nullable
    public String getPlayerPermission(PlayerIdentity player, String permission) {
        PermissionList map = getPlayerPermissions(player);
        if(map != null){
            return map.get(permission);
        }
        return null;
    }

    @Override
    public boolean checkPlayerPermission(PlayerIdentity player, String permission) {
        PermissionList map = getPlayerPermissions(player);
        if(map != null){
            String permValue = map.get(permission);
            return !PERMISSION_FALSE.equalsIgnoreCase(permValue);
        }
        return false;
    }

    @Override
    public Map<String, PermissionList> getAllGroupPermissions() {
        return this.groupPermissions;
    }

    @Override
    @Nullable
    public PermissionList getGroupPermissions(String group) {
        return this.groupPermissions.get(group);
    }

    @Override
    @Nullable
    public String getGroupPermission(String group, String permission) {
        PermissionList map = getGroupPermissions(group);
        if(map != null){
            return map.get(permission);
        }
        return null;
    }

    @Override
    public boolean checkGroupPermission(String group, String permission) {
        PermissionList map = getGroupPermissions(group);
        if(map != null){
            String permValue = map.get(permission);
            return !PERMISSION_FALSE.equalsIgnoreCase(permValue);
        }
        return false;
    }

    /*private Set<String> getPlayerGroups(PlayerIdentity ident){
        Set<String> result = new HashSet<String>();
        String groupsStr = getPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
        if(groupsStr != null && !groupsStr.isEmpty()){
            for(String g : groupsStr.replaceAll(" ", "").split(",")){
                if (!g.isEmpty()){
                    result.add(g);
                }
            }
        }
        return result;
    }*/

    /*public boolean addPlayerToGroup(PlayerIdentity ident, String group)
    {
        if (APIRegistry.getFEEventBus().post(new PermissionEvent.User.ModifyGroups(getServerZone(), ident, PermissionEvent.User.ModifyGroups.Action.ADD, group)))
            return false;
        Set<String> groups = getPlayerGroups(ident);
        groups.add(group);
        APIRegistry.perms.setPlayerPermissionProperty(ident, FEPermissions.PLAYER_GROUPS, StringUtils.join(groups, ","));
        return true;
    }

    public boolean removePlayerFromGroup(PlayerIdentity ident, String group)
    {
        if (APIRegistry.getFEEventBus().post(new PermissionEvent.User.ModifyGroups(getServerZone(), ident, PermissionEvent.User.ModifyGroups.Action.REMOVE, group)))
            return false;
        Set<String> groups = getPlayerGroups(ident);
        groups.remove(group);
        APIRegistry.perms.setPlayerPermissionProperty(ident, FEPermissions.PLAYER_GROUPS, StringUtils.join(groups, ","));
        return true;
    }

    public SortedSet<GroupEntry> getStoredPlayerGroups(PlayerIdentity ident)
    {
        SortedSet<GroupEntry> result = new TreeSet<GroupEntry>();
        String groupsStr = getPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
        if (groupsStr != null && !groupsStr.isEmpty())
            for (String group : groupsStr.replace(" ", "").split(","))
                result.add(new GroupEntry(getServerZone(), group));
        return result;
    }*/
}
