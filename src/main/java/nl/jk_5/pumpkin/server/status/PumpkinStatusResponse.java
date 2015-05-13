package nl.jk_5.pumpkin.server.status;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import net.minecraft.network.ServerStatusResponse;
import net.minecraft.server.MinecraftServer;

import nl.jk_5.pumpkin.api.MinecraftVersion;
import nl.jk_5.pumpkin.api.event.PumpkinEventFactory;
import nl.jk_5.pumpkin.api.event.server.StatusPingEvent;
import nl.jk_5.pumpkin.api.status.StatusClient;
import nl.jk_5.pumpkin.api.status.StatusResponse;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.text.PumpkinTexts;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

public final class PumpkinStatusResponse {

    private PumpkinStatusResponse() {
    }

    public static ServerStatusResponse post(MinecraftServer server, StatusClient client) {
        return call(create(server), client);
    }

    public static ServerStatusResponse postLegacy(MinecraftServer server, InetSocketAddress address, MinecraftVersion version, InetSocketAddress virtualHost) {
        ServerStatusResponse response = create(server);
        response.setProtocolVersionInfo(new ServerStatusResponse.MinecraftProtocolVersionIdentifier(response.getProtocolVersionInfo().getName(), Byte.MAX_VALUE));
        response = call(response, new PumpkinLegacyStatusClient(address, version, virtualHost));
        if(response != null && response.getPlayerCountData() == null){
            response.setPlayerCountData(new ServerStatusResponse.PlayerCountData(-1, 0));
        }
        return response;
    }

    private static ServerStatusResponse call(ServerStatusResponse response, StatusClient client) {
        if(!Pumpkin.instance().postEvent(PumpkinEventFactory.createStatusPing(client, ((StatusPingEvent.Response) response)))) {
            return response;
        }else{
            return null;
        }
    }

    public static ServerStatusResponse create(MinecraftServer server) {
        return clone(server.getServerStatusResponse());
    }

    private static ServerStatusResponse clone(ServerStatusResponse original) {
        ServerStatusResponse clone = new ServerStatusResponse();
        clone.setServerDescription(original.getServerDescription());
        if(original.getFavicon() != null){
            ((StatusPingEvent.Response) clone).setFavicon(((StatusResponse) original).getFavicon().get());
        }

        clone.setPlayerCountData(clone(original.getPlayerCountData()));
        clone.setProtocolVersionInfo(clone(original.getProtocolVersionInfo()));
        return clone;
    }

    private static ServerStatusResponse.PlayerCountData clone(ServerStatusResponse.PlayerCountData original) {
        ServerStatusResponse.PlayerCountData clone = new ServerStatusResponse.PlayerCountData(original.getMaxPlayers(), original.getOnlinePlayerCount());
        clone.setPlayers(original.getPlayers());
        return clone;
    }

    private static ServerStatusResponse.MinecraftProtocolVersionIdentifier clone(ServerStatusResponse.MinecraftProtocolVersionIdentifier original) {
        return new ServerStatusResponse.MinecraftProtocolVersionIdentifier(original.getName(), original.getProtocol());
    }

    private static String getFirstLine(String s) {
        int i = s.indexOf('\n');
        return i == -1 ? s : s.substring(0, i);
    }

    public static String getMotd(ServerStatusResponse response) {
        return getFirstLine(PumpkinTexts.toLegacy(response.getServerDescription()));
    }

    private static final Pattern STRIP_FORMATTING = Pattern.compile("§[0-9A-FK-OR]?", CASE_INSENSITIVE);

    public static String getUnformattedMotd(ServerStatusResponse response) {
        return getFirstLine(STRIP_FORMATTING.matcher(response.getServerDescription().getUnformattedText()).replaceAll(""));
    }

}