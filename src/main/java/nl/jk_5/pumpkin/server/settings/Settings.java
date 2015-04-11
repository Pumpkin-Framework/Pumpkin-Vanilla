package nl.jk_5.pumpkin.server.settings;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Settings {

    private static final Logger logger;
    private static final File configFile;
    private static final Config config;

    public static final Config minecraft;
    public static final ServerSettings server;
    public static final SqlSettings sql;
    public static final int lobbyMappack;

    static {
        logger = LogManager.getLogger();
        configFile = new File("settings.conf");

        logger.info("Loading config");

        if(!configFile.exists() || configFile.length() == 0){
            ReadableByteChannel in = null;
            FileChannel out = null;
            try{
                in = Channels.newChannel(Settings.class.getResourceAsStream("/config/default.conf"));
                out = new FileOutputStream(configFile).getChannel();
                out.transferFrom(in, 0, Long.MAX_VALUE);
            }catch(IOException e){
                logger.error("Error while creating default config file", e);
            }finally{
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }

        Config defaults = ConfigFactory.parseResources("/config/default.conf");
        config = ConfigFactory.parseFile(configFile).withFallback(defaults);

        minecraft = config.getConfig("minecraft");
        sql = new SqlSettings(config.getConfig("sql"));
        server = new ServerSettings(config.getConfig("server"));
        lobbyMappack = config.getInt("lobbyMappack");

        logger.info("Config is ready");
    }
}
