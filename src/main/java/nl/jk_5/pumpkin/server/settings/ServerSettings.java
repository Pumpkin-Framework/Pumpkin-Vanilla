package nl.jk_5.pumpkin.server.settings;

import com.typesafe.config.Config;

public class ServerSettings {

    public final String url;

    ServerSettings(Config config) {
        this.url = config.getString("url");
    }
}
