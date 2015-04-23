package nl.jk_5.pumpkin.server.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.server.Pumpkin;

class PacketProcessor {

    private static final Logger logger = LogManager.getLogger();

    static void handle(JsonObject packet){
        logger.info("Incoming json: " + packet.toString());
    }

    static void onOpen(Channel channel){
        logger.info("Channel ready to accept messages");
        JsonObject init = new JsonObject();

        JsonArray online = new JsonArray();
        for(GameProfile gameProfile : Pumpkin.instance().getPlayerManager().getOnlinePlayers()){
            JsonObject player = new JsonObject();
            player.addProperty("id", gameProfile.getId().toString());
            player.addProperty("name", gameProfile.getName());
            online.add(player);
        }
        init.add("online", online);
        init.addProperty("type", "init");

        Pumpkin.instance().getServerConnection().send(init);
    }
}
