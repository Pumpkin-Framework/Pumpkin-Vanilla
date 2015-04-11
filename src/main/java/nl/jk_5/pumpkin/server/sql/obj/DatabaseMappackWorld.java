package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.jk_5.pumpkin.api.mappack.MappackWorld;

@DatabaseTable(tableName = "mappack_world")
public class DatabaseMappackWorld implements MappackWorld {

    @DatabaseField(generatedId = true, unique = true)
    private int id;

    @DatabaseField(columnName = "map_id", foreign = true, foreignAutoRefresh = true)
    private DatabaseMappack map;

    @DatabaseField
    private String name;

    @DatabaseField
    private String generator;

    @DatabaseField
    private String dimension;

    @DatabaseField(columnName = "isdefault")
    private boolean isDefault;

    public DatabaseMappackWorld() {
    }

    public int getId() {
        return id;
    }

    public DatabaseMappack getMappack() {
        return map;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getGenerator() {
        return generator;
    }

    @Override
    public String getDimension() {
        return dimension;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }
}
