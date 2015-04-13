package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mappack_team")
public class DatabaseMappackTeam {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "map_id")
    private DatabaseMappack mappack;

    @DatabaseField(width = 16)
    private String name;

    public DatabaseMappackTeam() {
    }

    public int getId() {
        return id;
    }

    public DatabaseMappack getMappack() {
        return mappack;
    }
}
