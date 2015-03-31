package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mappack_world")
public class DatabaseMappackWorld {

    @DatabaseField(generatedId = true, unique = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private DatabaseMappack mappack;

    public DatabaseMappackWorld() {
    }

    public DatabaseMappackWorld(DatabaseMappack mappack) {
        this.mappack = mappack;
    }

    public int getId() {
        return id;
    }

    public DatabaseMappack getMappack() {
        return mappack;
    }
}
