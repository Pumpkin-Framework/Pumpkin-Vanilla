package nl.jk_5.pumpkin.server.util;

import java.sql.Connection;
import java.sql.SQLException;
import javax.annotation.Nullable;

public class SqlUtils {

    public static void close(@Nullable Connection conn){
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }
}
