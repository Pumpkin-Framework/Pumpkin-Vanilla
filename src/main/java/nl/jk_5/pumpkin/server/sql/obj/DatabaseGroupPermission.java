package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "group_permission")
public class DatabaseGroupPermission {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String permission;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "group_id")
    private DatabaseGroup group;

    @DatabaseField
    private String value;

    public int getId() {
        return id;
    }

    public String getPermission() {
        return permission;
    }

    public DatabaseGroup getGroup() {
        return group;
    }

    public String getValue() {
        return value;
    }
}
