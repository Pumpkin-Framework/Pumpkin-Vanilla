package nl.jk_5.pumpkin.server.sql;

import com.google.common.cache.*;
import com.google.common.collect.ImmutableMap;
import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.Driver;

import nl.jk_5.pumpkin.server.settings.Settings;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;
import nl.jk_5.pumpkin.server.util.interfaces.SqlService;

import java.io.Closeable;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

@NonnullByDefault
public class SqlServiceImpl implements SqlService, Closeable {

    private static final Map<String, Properties> PROTOCOL_SPECIFIC_PROPS;

    static {
        ImmutableMap.Builder<String, Properties> build = ImmutableMap.builder();
        final Properties mySqlProps = new Properties();
        mySqlProps.setProperty("useConfigs",
                "maxPerformance"); // Config options based on http://assets.en.oreilly
                // .com/1/event/21/Connector_J%20Performance%20Gems%20Presentation.pdf
        build.put("com.mysql.jdbc.Driver", mySqlProps);
        build.put("org.mariadb.jdbc.Driver", mySqlProps);

        PROTOCOL_SPECIFIC_PROPS = build.build();
    }

    private final LoadingCache<ConnectionInfo, HikariDataSource> connectionCache =
            CacheBuilder.newBuilder().removalListener(new RemovalListener<ConnectionInfo, HikariDataSource>() {
                @Override
                public void onRemoval(RemovalNotification<ConnectionInfo, HikariDataSource> notification) {
                    HikariDataSource source = notification.getValue();
                    if (source != null) {
                        source.close();
                    }
                }
            }).build(new CacheLoader<ConnectionInfo, HikariDataSource>() {
                @Override
                public HikariDataSource load(@Nonnull ConnectionInfo key) throws Exception {
                    HikariConfig config = new HikariConfig();
                    config.setUsername(key.getUser());
                    config.setPassword(key.getPassword());
                    config.setDriverClassName(key.getDriverClassName());
                    // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing for info on pool sizing
                    config.setMaximumPoolSize((Runtime.getRuntime().availableProcessors() * 2) + 1);
                    Properties driverSpecificProperties = PROTOCOL_SPECIFIC_PROPS.get(key.getDriverClassName());
                    if (driverSpecificProperties != null) {
                        config.setDataSourceProperties(driverSpecificProperties);
                    }
                    config.setJdbcUrl(key.getAuthlessUrl());
                    return new HikariDataSource(config);
                }
            });

    @Override
    public DataSource getDataSource() throws SQLException {
        ConnectionInfo info = ConnectionInfo.fromUrl(Settings.sql.jdbcUrl);
        try {
            return this.connectionCache.get(info);
        } catch (ExecutionException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void close() throws IOException {
        this.connectionCache.invalidateAll();
    }

    @Override
    public ConnectionSource getConnectionSource() throws SQLException {
        PostgresDatabaseType type = new PostgresDatabaseType();
        type.setDriver(new Driver());
        return new DataSourceConnectionSource(getDataSource(), type);
    }

    public static class ConnectionInfo {

        private static final Pattern URL_REGEX = Pattern.compile("(?:jdbc:)?([^:]+):(//)?(?:([^:]+)(?::([^@]+))?@)?(.*)");
        @Nullable private final String user;
        @Nullable private final String password;
        private final String driverClassName;
        private final String authlessUrl;
        private final String fullUrl;

        /**
         * Create a new ConnectionInfo with the give parameters
         * @param user The username to use when connecting to th database
         * @param password The password to connect with. If user is not null, password must not be null
         * @param driverClassName The class name of the driver to use for this connection
         * @param authlessUrl A JDBC url for this driver not containing authentication information
         * @param fullUrl The full jdbc url containing user, password, and database info
         */
        public ConnectionInfo(@Nullable String user, @Nullable String password, String driverClassName, String authlessUrl, String fullUrl) {
            this.user = user;
            this.password = password;
            this.driverClassName = driverClassName;
            this.authlessUrl = authlessUrl;
            this.fullUrl = fullUrl;
        }

        @Nullable
        public String getUser() {
            return this.user;
        }

        @Nullable
        public String getPassword() {
            return this.password;
        }

        public String getDriverClassName() {
            return this.driverClassName;
        }

        public String getAuthlessUrl() {
            return this.authlessUrl;
        }

        public String getFullUrl() {
            return this.fullUrl;
        }

        /**
         * Extracts the connection info from a JDBC url with additional authentication information as specified in {@link SqlService}.
         *
         * @param fullUrl The full JDBC URL as specified in SqlService
         * @return A constructed ConnectionInfo object using the info from the provided URL
         * @throws SQLException If the driver for the given URL is not present
         */
        public static ConnectionInfo fromUrl(String fullUrl) throws SQLException {
            Matcher match = URL_REGEX.matcher(fullUrl);
            if (!match.matches()) {
                throw new IllegalArgumentException("URL " + fullUrl + " is not a valid JDBC URL");
            }

            final String protocol = match.group(1);
            final boolean hasSlashes = match.group(2) != null;
            final String user = match.group(3);
            final String pass = match.group(4);
            final String serverDatabaseSpecifier = match.group(5);
            final String unauthedUrl = "jdbc:" + protocol + (hasSlashes ? "://" : ":") + serverDatabaseSpecifier;
            final String driverClass = DriverManager.getDriver(unauthedUrl).getClass().getCanonicalName();
            return new ConnectionInfo(user, pass, driverClass, unauthedUrl, fullUrl);
        }
    }
}
