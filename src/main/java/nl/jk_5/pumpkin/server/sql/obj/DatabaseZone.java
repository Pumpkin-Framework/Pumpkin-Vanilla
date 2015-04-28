package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "zone")
public class DatabaseZone {

    @DatabaseField(uniqueCombo = true, generatedId = true)
    private int id;

    @DatabaseField(uniqueCombo = true, canBeNull = true)
    private String area;

    @DatabaseField(uniqueCombo = true, canBeNull = true)
    private String shape;

    @DatabaseField(uniqueCombo = true, canBeNull = true)
    private String name;

    @DatabaseField(uniqueCombo = true, width = 8)
    private String type;

    @DatabaseField(uniqueCombo = true, columnName = "map_id", foreign = true, foreignAutoRefresh = true, canBeNull = true)
    private DatabaseMappack mappack;

    @DatabaseField(uniqueCombo = true, columnName = "world_id", foreign = true, foreignAutoRefresh = true, canBeNull = true)
    private DatabaseMappackWorld world;

    @DatabaseField(uniqueCombo = true, columnName = "team_id", foreign = true, foreignAutoRefresh = true, canBeNull = true)
    private DatabaseMappackWorld team;

    @ForeignCollectionField
    private ForeignCollection<DatabaseGroup> groups;

    public int getId() {
        return id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DatabaseMappack getMappack() {
        return mappack;
    }

    public DatabaseMappackWorld getWorld() {
        return world;
    }

    public ForeignCollection<DatabaseGroup> getGroups() {
        return groups;
    }
}
