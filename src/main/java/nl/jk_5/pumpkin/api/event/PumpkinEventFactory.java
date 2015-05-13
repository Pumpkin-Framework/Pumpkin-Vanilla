package nl.jk_5.pumpkin.api.event;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.api.event.map.PlayerJoinMapEvent;
import nl.jk_5.pumpkin.api.event.map.PlayerLeaveMapEvent;
import nl.jk_5.pumpkin.api.event.permission.RegisterPermissionsEvent;
import nl.jk_5.pumpkin.api.event.player.*;
import nl.jk_5.pumpkin.api.event.server.StatusPingEvent;
import nl.jk_5.pumpkin.api.event.world.PlayerJoinWorldEvent;
import nl.jk_5.pumpkin.api.event.world.PlayerLeaveWorldEvent;
import nl.jk_5.pumpkin.api.status.StatusClient;
import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.permissions.PermissionRegistrar;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.event.generator.ClassGeneratorProvider;
import nl.jk_5.pumpkin.server.util.event.generator.EventFactory;
import nl.jk_5.pumpkin.server.util.event.generator.FactoryProvider;
import nl.jk_5.pumpkin.server.util.event.generator.NullPolicy;
import nl.jk_5.pumpkin.server.util.location.Location;

import java.net.InetSocketAddress;
import javax.annotation.Nullable;

public final class PumpkinEventFactory {

    private static final FactoryProvider factoryProvider;
    private static final LoadingCache<Class<?>, EventFactory<?>> factories;

    static {
        factoryProvider = new ClassGeneratorProvider("nl.jk_5.pumpkin.api.event.impl");
        factoryProvider.setNullPolicy(NullPolicy.NON_NULL_BY_DEFAULT);

        factories = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, EventFactory<?>>() {
            @Override
            public EventFactory<?> load(Class<?> type) {
                return factoryProvider.create(type, AbstractEvent.class);
            }
        });
    }

    private PumpkinEventFactory() {
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private static <T> T createEvent(Class<T> type, java.util.Map<String, Object> values) {
        return (T) factories.getUnchecked(type).apply(values);
    }

    public static PlayerJoinMapEvent createPlayerJoinMapEvent(Map map, Player player){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(3);
        values.put("map", map);
        values.put("player", player);
        values.put("user", player.getUser());
        return createEvent(PlayerJoinMapEvent.class, values);
    }

    public static PlayerLeaveMapEvent createPlayerLeaveMapEvent(Map map, Player player){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(3);
        values.put("map", map);
        values.put("player", player);
        values.put("user", player.getUser());
        return createEvent(PlayerLeaveMapEvent.class, values);
    }

    public static PlayerJoinWorldEvent createPlayerJoinWorldEvent(MapWorld world, Player player){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(3);
        values.put("world", world);
        values.put("player", player);
        values.put("user", player.getUser());
        return createEvent(PlayerJoinWorldEvent.class, values);
    }

    public static PlayerLeaveWorldEvent createPlayerLeaveWorldEvent(MapWorld world, Player player){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(3);
        values.put("world", world);
        values.put("player", player);
        values.put("user", player.getUser());
        return createEvent(PlayerLeaveWorldEvent.class, values);
    }

    public static PlayerChatEvent createPlayerChatEvent(Player player, String originalMessage, Text message){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(4);
        values.put("player", player);
        values.put("originalMessage", originalMessage);
        values.put("message", message);
        values.put("user", player.getUser());
        return createEvent(PlayerChatEvent.class, values);
    }

    public static RegisterPermissionsEvent createRegisterPermissionsEvent(PermissionRegistrar registrar){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(1);
        values.put("registrar", registrar);
        return createEvent(RegisterPermissionsEvent.class, values);
    }

    public static PlayerPreJoinServerEvent createPlayerPreJoinServerEvent(Player player, InetSocketAddress address, Text joinMessage, Location spawnPoint, @Nullable Location location){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(7);
        values.put("player", player);
        values.put("address", address);
        values.put("kickMessage", null);
        values.put("joinMessage", joinMessage);
        values.put("spawnPoint", spawnPoint);
        values.put("location", location);
        values.put("user", player.getUser());
        return createEvent(PlayerPreJoinServerEvent.class, values);
    }

    public static PlayerPostJoinServerEvent createPlayerPostJoinServerEvent(Player player){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(2);
        values.put("player", player);
        values.put("user", player.getUser());
        return createEvent(PlayerPostJoinServerEvent.class, values);
    }

    public static PlayerLeaveServerEvent createPlayerLeaveServerEvent(Player player, IChatComponent leaveMessage){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(3);
        values.put("player", player);
        values.put("leaveMessage", leaveMessage);
        values.put("user", player.getUser());
        return createEvent(PlayerLeaveServerEvent.class, values);
    }

    public static PlayerPreRespawnEvent createPlayerPreRespawnEvent(Player player, Location deathLocation, Location respawnLocation){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(4);
        values.put("player", player);
        values.put("deathLocation", deathLocation);
        values.put("respawnLocation", respawnLocation);
        values.put("user", player.getUser());
        return createEvent(PlayerPreRespawnEvent.class, values);
    }

    public static PlayerPostRespawnEvent createPlayerPostRespawnEvent(Player player){
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(2);
        values.put("player", player);
        values.put("user", player.getUser());
        return createEvent(PlayerPostRespawnEvent.class, values);
    }

    /**
     * Creates a new {@link StatusPingEvent}.
     *
     * @param client The client that is pinging the server
     * @param response The response to send to the client
     * @return A new instance of the event
     */
    public static StatusPingEvent createStatusPing(StatusClient client, StatusPingEvent.Response response) {
        java.util.Map<String, Object> values = Maps.newHashMapWithExpectedSize(2);
        values.put("client", client);
        values.put("response", response);
        return createEvent(StatusPingEvent.class, values);
    }
}
