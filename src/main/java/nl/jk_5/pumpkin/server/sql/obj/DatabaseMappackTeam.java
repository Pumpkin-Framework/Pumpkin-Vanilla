package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mappack_team")
public class DatabaseMappackTeam {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private DatabaseTeam team;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private DatabaseMappack mappack;

    public DatabaseMappackTeam() {
    }

    public DatabaseMappackTeam(DatabaseTeam team, DatabaseMappack mappack) {
        this.team = team;
        this.mappack = mappack;
    }

    public int getId() {
        return id;
    }

    public DatabaseTeam getTeam() {
        return team;
    }

    public DatabaseMappack getMappack() {
        return mappack;
    }
}
