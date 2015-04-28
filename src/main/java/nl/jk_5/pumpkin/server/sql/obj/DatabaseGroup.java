package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "groups")
public class DatabaseGroup {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(width = 32)
    private String name;

    @DatabaseField(columnName = "zone_id", foreign = true, foreignAutoRefresh = true)
    private DatabaseZone zone;

    @ForeignCollectionField
    private ForeignCollection<DatabaseGroupPermission> permissions;

    @ForeignCollectionField
    private ForeignCollection<DatabaseGroupMembership> memberships;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DatabaseZone getZone() {
        return zone;
    }

    public ForeignCollection<DatabaseGroupPermission> getPermissions() {
        return permissions;
    }

    public ForeignCollection<DatabaseGroupMembership> getMemberships() {
        return memberships;
    }
}
