package nl.jk_5.pumpkin.server.player;

import com.mojang.authlib.GameProfile;
import jk_5.eventbus.EventHandler;

import nl.jk_5.pumpkin.server.event.player.PlayerJoinServerEvent;
import nl.jk_5.pumpkin.server.event.player.PlayerLeaveServerEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerManager {

    private final List<GameProfile> onlinePlayers = new ArrayList<GameProfile>();
    private final List<GameProfile> unmodifiableOnlinePlayers = Collections.unmodifiableList(onlinePlayers);

    @EventHandler
    public void onJoin(PlayerJoinServerEvent.Post event){
        this.onlinePlayers.add(event.getPlayer().getGameProfile());
    }

    @EventHandler
    public void onLeave(PlayerLeaveServerEvent event){
        this.onlinePlayers.remove(event.getPlayer().getGameProfile());
    }

    public List<GameProfile> getOnlinePlayers() {
        return this.unmodifiableOnlinePlayers;
    }
}
