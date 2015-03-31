package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import nl.jk_5.pumpkin.api.mappack.Mappack;
import nl.jk_5.pumpkin.api.mappack.MappackAuthor;

import java.util.Collection;

@DatabaseTable(tableName = "mappacks")
public class DatabaseMappack implements Mappack {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @ForeignCollectionField
    private ForeignCollection<DatabaseMappackAuthor> authors;

    @ForeignCollectionField
    private ForeignCollection<DatabaseMappackTeam> teams;

    @ForeignCollectionField
    private ForeignCollection<DatabaseMappackWorld> worlds;

    public DatabaseMappack() {
    }

    public DatabaseMappack(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @Override
    public Collection<MappackAuthor> getAuthors() {
        //TODO: this is ugly, but java compiler is being stupid
        //noinspection unchecked
        return ((Collection) this.authors);
    }

    public Collection<DatabaseMappackTeam> getTeams() {
        return teams;
    }

    public Collection<DatabaseMappackWorld> getWorlds() {
        return worlds;
    }
}
