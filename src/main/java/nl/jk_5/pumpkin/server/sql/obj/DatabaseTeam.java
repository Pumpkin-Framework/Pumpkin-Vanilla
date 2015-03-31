package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "team")
public class DatabaseTeam {

    @DatabaseField(generatedId = true, unique = true)
    private int id;

    @DatabaseField
    private String name;

    public DatabaseTeam() {
    }

    public DatabaseTeam(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
