package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.jk_5.pumpkin.api.mappack.GameRule;

@DatabaseTable(tableName = "gamerule")
public class DatabaseGamerule implements GameRule {

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "map_id")
    private DatabaseMappack mappack;

    @DatabaseField(width = 32)
    private String key;

    @DatabaseField(width = 16)
    private String value;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
}
