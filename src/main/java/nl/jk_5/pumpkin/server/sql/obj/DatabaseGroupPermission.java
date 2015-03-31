package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "group_permission")
public class DatabaseGroupPermission {

    @DatabaseField(columnName = "zone_id", uniqueCombo = true, foreign = true)
    private DatabaseZone zone;

    @DatabaseField(uniqueCombo = true)
    private String permission;

    @DatabaseField(uniqueCombo = true)
    private String group;

    @DatabaseField
    private String value;
}
