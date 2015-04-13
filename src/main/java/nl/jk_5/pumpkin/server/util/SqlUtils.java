package nl.jk_5.pumpkin.server.util;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;
import javax.annotation.Nullable;

public final class SqlUtils {

    private SqlUtils() {
    }

    public static void close(@Nullable Connection conn){
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    public static void close(@Nullable ConnectionSource conn){
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }
}
