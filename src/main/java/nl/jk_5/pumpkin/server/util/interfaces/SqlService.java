package nl.jk_5.pumpkin.server.util.interfaces;

import java.sql.SQLException;
import javax.annotation.concurrent.ThreadSafe;
import javax.sql.DataSource;

@ThreadSafe
public interface SqlService {

    /**
     * Returns a data source
     *
     * @return A data source providing connections to the given URL.
     * @throws java.sql.SQLException if a connection to the database could not be established
     */
    DataSource getDataSource() throws SQLException;
}
