package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
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

    @DatabaseField(uniqueCombo = true, columnName = "parent_id", canBeNull = true)
    private int parentId;

    @DatabaseField
    private int type;

    @DatabaseField(uniqueCombo = true, canBeNull = true)
    private int dimension;
}
