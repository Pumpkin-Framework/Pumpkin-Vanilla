package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user_permission")
public class DatabaseUserPermission {

    @DatabaseField(columnName = "zone_id", uniqueCombo = true, foreign = true)
    private DatabaseZone zone;

    @DatabaseField(uniqueCombo = true)
    private String permission;

    @DatabaseField(foreign = true, uniqueCombo = true, columnName = "user_id")
    private DatabaseUser user;

    @DatabaseField
    private String value;
}
