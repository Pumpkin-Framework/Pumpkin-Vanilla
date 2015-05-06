package nl.jk_5.pumpkin.server.web;

import com.google.gson.JsonObject;

import nl.jk_5.pumpkin.api.event.EventHandler;
import nl.jk_5.pumpkin.api.event.player.PlayerLeaveServerEvent;
import nl.jk_5.pumpkin.api.event.player.PlayerPostJoinServerEvent;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
public final class WebEventHandler {

    private static final WebEventHandler INSTANCE = new WebEventHandler();

    private WebEventHandler() {
    }

    private static void send(JsonObject obj){
        Pumpkin.instance().getServerConnection().send(obj);
    }

    @EventHandler
    public void onPlayerJoin(PlayerPostJoinServerEvent event){
        JsonObject pack = new JsonObject();
        JsonObject player = new JsonObject();
        pack.addProperty("type", "player-join");

        player.addProperty("id", event.getPlayer().getGameProfile().getId().toString());
        player.addProperty("name", event.getPlayer().getGameProfile().getName());

        pack.add("player", player);

        send(pack);
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveServerEvent event){
        JsonObject pack = new JsonObject();
        JsonObject player = new JsonObject();
        pack.addProperty("type", "player-leave");

        player.addProperty("id", event.getPlayer().getGameProfile().getId().toString());
        player.addProperty("name", event.getPlayer().getGameProfile().getName());

        pack.add("player", player);

        send(pack);
    }

    public static WebEventHandler instance() {
        return INSTANCE;
    }
}
