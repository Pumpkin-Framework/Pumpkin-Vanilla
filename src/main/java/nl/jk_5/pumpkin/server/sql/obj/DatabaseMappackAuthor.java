package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.jk_5.pumpkin.api.mappack.MappackAuthor;
import nl.jk_5.pumpkin.api.user.User;

@DatabaseTable(tableName = "mappack_author")
public class DatabaseMappackAuthor implements MappackAuthor {

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private DatabaseMappack mappack;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private DatabaseUser author;

    @DatabaseField
    private String role;

    public DatabaseMappackAuthor() {
    }

    public DatabaseMappackAuthor(DatabaseMappack mappack, DatabaseUser author, String role) {
        this.mappack = mappack;
        this.author = author;
        this.role = role;
    }

    public DatabaseMappack getMappack() {
        return mappack;
    }

    public String getRole() {
        return role;
    }

    @Override
    public User getUser() {
        return this.author;
    }
}
