package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "game_event")
public class DatabaseGameEvent {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(width = 32)
    private String type;

    @DatabaseField(width = 32, columnName = "subtype")
    private String subType;

    @DatabaseField(columnName = "player_1", canBeNull = true, foreign = true, foreignAutoRefresh = true)
    private DatabaseUser user1;

    @DatabaseField(columnName = "player_2", canBeNull = true, foreign = true, foreignAutoRefresh = true)
    private DatabaseUser user2;

    @DatabaseField(columnName = "game_id", foreign = true, foreignAutoRefresh = true)
    private DatabaseGame game;
}
