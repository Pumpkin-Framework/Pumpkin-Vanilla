package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "group_membership")
public class DatabaseGroupMembership {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = "group_id", foreignAutoRefresh = true)
    private DatabaseGroup group;

    @DatabaseField(foreign = true, columnName = "user_id", foreignAutoRefresh = true)
    private DatabaseUser user;

    public int getId() {
        return id;
    }

    public DatabaseGroup getGroup() {
        return group;
    }

    public DatabaseUser getUser() {
        return user;
    }
}
