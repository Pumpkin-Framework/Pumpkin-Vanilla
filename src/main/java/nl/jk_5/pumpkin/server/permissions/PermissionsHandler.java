package nl.jk_5.pumpkin.server.permissions;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.api.event.EventHandler;
import nl.jk_5.pumpkin.api.event.Order;
import nl.jk_5.pumpkin.api.event.PumpkinEventFactory;
import nl.jk_5.pumpkin.api.event.permission.RegisterPermissionsEvent;
import nl.jk_5.pumpkin.api.event.player.PlayerChatEvent;
import nl.jk_5.pumpkin.api.event.player.PlayerPostJoinServerEvent;
import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.permissions.zone.RootZone;
import nl.jk_5.pumpkin.server.permissions.zone.ServerZone;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.sql.SqlTableManager;
import nl.jk_5.pumpkin.server.sql.obj.DatabaseZone;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.sql.SQLException;
import java.util.List;
import javax.annotation.Nullable;

@NonnullByDefault
public class PermissionsHandler implements PermissionRegistrar {

    private static final Logger logger = LogManager.getLogger();

    private boolean registering = true;

    private RootZone rootZone = new RootZone();
    private ServerZone serverZone;

    @Override
    public void register(String name, String value) {
        if(!registering){
            throw new IllegalStateException("Registrations are not allowed at this point");
        }

        rootZone.getPermissionsList().set(name, value);
    }

    @Override
    public void register(String name, boolean value) {
        register(name, value ? "true" : "false");
    }

    public void endRegistrations() {
        RegisterPermissionsEvent event = PumpkinEventFactory.createRegisterPermissionsEvent(this);
        Pumpkin.instance().postEvent(event);

        registering = false;

        rootZone.getPermissionsList().dump();

        logger.info("Reading permissions from database");

        try {
            List<DatabaseZone> res = SqlTableManager.zoneDao.queryForEq("type", "server");
            if(res.isEmpty()){
                DatabaseZone zone = new DatabaseZone();
                zone.setType("server");
                zone.setName("_SERVER_");
                SqlTableManager.zoneDao.create(zone);
                serverZone = new ServerZone(zone);
            }else{
                serverZone = new ServerZone(res.get(0));
            }
        } catch (SQLException e) {
            logger.error("Failed to load server zone from database", e);
        }

        if(!serverZone.groupExists(PumpkinPermissions.GROUP_ALL)){
            PermissionsList group = serverZone.createGroup(PumpkinPermissions.GROUP_ALL);
            group.set(PumpkinPermissions.PERM_GROUP_PRIORITY, 0);
        }
        if(!serverZone.groupExists(PumpkinPermissions.GROUP_GUESTS)){
            PermissionsList group = serverZone.createGroup(PumpkinPermissions.GROUP_GUESTS);
            group.set(PumpkinPermissions.PERM_GROUP_PRIORITY, 10);
            group.set("pumpkin.group.prefix", "[Guest]");
        }
        if(!serverZone.groupExists(PumpkinPermissions.GROUP_INGAME)){
            PermissionsList group = serverZone.createGroup(PumpkinPermissions.GROUP_INGAME);
            group.set(PumpkinPermissions.PERM_GROUP_PRIORITY, 100);
            group.set("pumpkin.group.prefix", "[Ingame]");
        }

        /*if(!serverZone.groupExists(Zone.GROUP_ALL)){
            serverZone.setGroupPermission(Zone.GROUP_ALL, PumpkinPermissions.GROUP, true);
            serverZone.setGroupPermission(Zone.GROUP_ALL, PumpkinPermissions.GROUP_PRIORITY, "0");
        }
        if(!serverZone.groupExists(Zone.GROUP_GUESTS)){
            serverZone.setGroupPermission(Zone.GROUP_GUESTS, PumpkinPermissions.GROUP, true);
            serverZone.setGroupPermission(Zone.GROUP_GUESTS, PumpkinPermissions.GROUP_PRIORITY, "10");
            //serverZone.setGroupPermission(Zone.GROUP_GUESTS, PumpkinPermissions.PREFIX, "[GUEST]"); //TODO: chat prefix
        }
        if(!serverZone.groupExists(Zone.GROUP_INGAME)){
            serverZone.setGroupPermission(Zone.GROUP_INGAME, PumpkinPermissions.GROUP, true);
            serverZone.setGroupPermission(Zone.GROUP_INGAME, PumpkinPermissions.GROUP_PRIORITY, "100");
            //serverZone.setGroupPermission(Zone.GROUP_OPERATORS, PumpkinPermissions.PREFIX, "[INGAME]"); //TODO: chat prefix
        }*/
    }

    @Nullable
    public String getPermission(String user, String permission, @Nullable Map map){
        if(map != null){
            String value = map.getPermissionsHandler().getPermission(user, permission);

            if(value != null){
                return value;
            }
        }

        String value = serverZone.getPermissionValue(user, permission);
        if(value != null){
            return value;
        }

        value = rootZone.getPermissionValue(user, permission);
        if(value != null){
            return value;
        }

        logger.warn("No default value registered for permission " + permission);

        return null;
    }

    @Nullable
    public String getPermission(String user, String permission){
        return getPermission(user, permission, null);
    }

    public boolean hasPermission(String user, String permission, @Nullable Map map) {
        String value = getPermission(user, permission, map);
        return value != null && value.equals(PumpkinPermissions.TRUE);
    }

    public boolean hasPermission(Player player, String permission){
        if(player.getUser() != null){
            return hasPermission("user:" + player.getUser().getId(), permission, player.isEditorMode() ? null : player.getMap());
        }else{
            return hasPermission("player:" + player.getUuid().toString(), permission, player.isEditorMode() ? null : player.getMap());
        }
    }

    @Nullable
    public String getPermission(Player player, String permission){
        if(player.getUser() != null){
            return getPermission("user:" + player.getUser().getId(), permission, player.isEditorMode() ? null : player.getMap());
        }else{
            return getPermission("player:" + player.getUuid().toString(), permission, player.isEditorMode() ? null : player.getMap());
        }
    }

    @EventHandler(order = Order.EARLY)
    public void playerLogin(PlayerPostJoinServerEvent e){
        User user = e.getUser();

        if(user == null){
            serverZone.addPlayerToGroup(e.getPlayer(), PumpkinPermissions.GROUP_GUESTS);
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent e){
        //TODO: group priorities
        //TODO: multiple prefixes

        String prefix = getPermission(e.getPlayer(), "pumpkin.group.prefix");
        if(prefix == null || prefix.isEmpty()) return;

        IChatComponent comp = new ChatComponentText("");
        comp.appendText(prefix);
        comp.appendText(" ");
        IChatComponent name = e.getPlayer().getEntity().getDisplayName();
        name.getChatStyle().setColor(EnumChatFormatting.GRAY);
        comp.appendSibling(name);
        name = new ChatComponentText(": ");
        name.getChatStyle().setColor(EnumChatFormatting.GRAY);
        comp.appendSibling(name);
        name = new ChatComponentText(e.getOriginalMessage());
        name.getChatStyle().setColor(EnumChatFormatting.GRAY);
        comp.appendSibling(name);

        e.setMessage(comp);
    }
}
