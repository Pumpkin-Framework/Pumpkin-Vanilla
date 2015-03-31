package nl.jk_5.pumpkin.server.permission.api;

import net.minecraft.entity.player.EntityPlayer;

import nl.jk_5.pumpkin.server.util.Location;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.Map;
import javax.annotation.Nullable;

@NonnullByDefault
public interface Zone {

    /**
     * Gets the unique zone-ID
     */
    @Deprecated
    int getId();

    String getName();

    /**
     * Checks, whether the player is in the zone.
     *
     * @param player
     */
    boolean isPlayerInZone(EntityPlayer player);

    /**
     * Checks, whether the location is in the zone.
     *
     * @param location
     */
    boolean isInZone(Location location);

    /**
     * Checks, whether the given area is entirely contained within this zone.
     *
     * @param point
     */
    boolean isInZone(Area point);

    /**
     * Checks, whether a part of the given area is in this zone.
     *
     * @param point
     */
    boolean isPartOfZone(Area point);

    Map<PlayerIdentity, PermissionList> getAllPlayerPermissions();

    @Nullable
    PermissionList getPlayerPermissions(PlayerIdentity player);

    @Nullable
    String getPlayerPermission(PlayerIdentity player, String permission);

    boolean checkPlayerPermission(PlayerIdentity player, String permission);

    Map<String, PermissionList> getAllGroupPermissions();

    @Nullable
    PermissionList getGroupPermissions(String group);

    @Nullable
    String getGroupPermission(String group, String permission);

    boolean checkGroupPermission(String group, String permission);

    @Nullable
    Zone getParent();
}
