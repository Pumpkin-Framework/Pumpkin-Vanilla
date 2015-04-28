package nl.jk_5.pumpkin.server.permissions.zone;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.permissions.PermissionsList;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseGroup;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseGroupMembership;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseGroupPermission;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseZone;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@NonnullByDefault
public abstract class AbstractZone implements Zone {

    protected final DatabaseZone zoneInfo;

    protected final Set<String> groups = new HashSet<String>();
    protected final Map<String, PermissionsList> playerPermissions = new HashMap<String, PermissionsList>();
    protected final Map<String, PermissionsList> groupPermissions = new HashMap<String, PermissionsList>();
    protected final Multimap<String, String> groupMemberships = ArrayListMultimap.create();

    protected AbstractZone(DatabaseZone zoneInfo) {
        this.zoneInfo = zoneInfo;

        //TODO: read user permissions from 'user_permission' table into 'playerPermissions' map
        for (DatabaseGroup group : zoneInfo.getGroups()) {
            String groupId = "db:" + group.getId();
            PermissionsList perms = createGroup(groupId);
            for (DatabaseGroupMembership membership : group.getMemberships()) {
                groupMemberships.put("user:" + membership.getUser().getId(), groupId);
            }
            for (DatabaseGroupPermission perm : group.getPermissions()) {
                perms.set(perm.getPermission(), perm.getValue());
            }
        }
    }

    public boolean groupExists(String group){
        return groups.contains(group);
    }

    public PermissionsList createGroup(String group){
        if(groupPermissions.containsKey(group)){
            return groupPermissions.get(group);
        }
        PermissionsList list = new PermissionsList();
        groupPermissions.put(group, list);
        groups.add(group);
        return list;
    }

    public void addPlayerToGroup(Player player, String group){
        addToGroup("player:" + player.getUuid().toString(), group);
    }

    public void addUserToGroup(User user, String group){
        addToGroup("user:" + user.getId(), group);
    }

    public void addToGroup(String el, String group){
        groupMemberships.put(el, group);
    }

    @Nullable
    @Override
    public String getPermissionValue(String userId, String permission) {
        //TODO: handle group priorities

        PermissionsList playerPermissionList = playerPermissions.get(userId);
        String value;
        if(playerPermissionList != null){
            value = playerPermissionList.get(permission);
            if(value != null){
                return value;
            }
        }
        for (String groupId : groupMemberships.get(userId)) {
            value = groupPermissions.get(groupId).get(permission);
            if(value != null){
                return value;
            }
        }
        return null;
    }
}
