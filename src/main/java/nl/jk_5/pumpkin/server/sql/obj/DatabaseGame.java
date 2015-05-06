package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "game")
public class DatabaseGame {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "mappacks", foreign = true, foreignAutoRefresh = true)
    private DatabaseMappack mappack;

    @DatabaseField
    private Date started;
}
