package nl.jk_5.pumpkin.server.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;

import nl.jk_5.pumpkin.api.event.EventHandler;
import nl.jk_5.pumpkin.api.event.player.PlayerLeaveServerEvent;
import nl.jk_5.pumpkin.api.event.player.PlayerPostJoinServerEvent;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;

@NonnullByDefault
public class PlayerManager {

    private final Collection<GameProfile> onlineProfiles = new ProfilesList(this);

    private final List<Player> players = new CopyOnWriteArrayList<Player>();
    private final Map<String, Player> playersByName = new HashMap<String, Player>();

    final List<Player> onlinePlayers = new CopyOnWriteArrayList<Player>();
    final Map<UUID, Player> playersById = new HashMap<UUID, Player>();

    public Player getFromEntity(EntityPlayerMP entity){
        //noinspection ConstantConditions
        return this.getById(entity.getGameProfile().getId());
    }

    public Player getOrCreatePlayer(EntityPlayerMP entity) {
        Player player = this.getFromEntity(entity);
        //noinspection ConstantConditions
        if(player == null){
            player = new Player(entity.getGameProfile());
            this.players.add(player);
            this.playersById.put(entity.getGameProfile().getId(), player);
            this.playersByName.put(entity.getGameProfile().getName(), player);
            return player;
        }else{
            return player;
        }
    }

    @EventHandler
    public void onJoin(PlayerPostJoinServerEvent event){
        this.onlinePlayers.add(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerLeaveServerEvent event){
        this.onlinePlayers.remove(event.getPlayer());
    }

    @Nullable
    public Player getById(UUID id){
        return this.playersById.get(id);
    }

    public Collection<GameProfile> getOnlineProfiles() {
        return this.onlineProfiles;
    }

    public Collection<Player> getOnlinePlayers() {
        return this.onlinePlayers;
    }
}
