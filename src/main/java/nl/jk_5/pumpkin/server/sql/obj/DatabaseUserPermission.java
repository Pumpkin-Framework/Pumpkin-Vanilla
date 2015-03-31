package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "user_permission")
public class DatabaseUserPermission {

    @DatabaseField(columnName = "zone_id", uniqueCombo = true, foreign = true)
    private DatabaseZone zone;

    @DatabaseField(uniqueCombo = true)
    private String permission;

    @DatabaseField(uniqueCombo = true)
    private UUID user;

    @DatabaseField
    private String value;
}
