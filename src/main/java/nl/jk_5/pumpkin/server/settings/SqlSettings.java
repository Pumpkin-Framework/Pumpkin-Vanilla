package nl.jk_5.pumpkin.server.settings;

import com.typesafe.config.Config;

public class SqlSettings {

    public final String jdbcUrl;

    SqlSettings(Config config) {
        this.jdbcUrl = config.getString("jdbc-url");
    }
}
