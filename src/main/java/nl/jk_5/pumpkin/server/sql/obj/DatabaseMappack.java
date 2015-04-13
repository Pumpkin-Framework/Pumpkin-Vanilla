package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import nl.jk_5.pumpkin.api.mappack.Mappack;
import nl.jk_5.pumpkin.api.mappack.MappackAuthor;
import nl.jk_5.pumpkin.api.mappack.MappackFile;
import nl.jk_5.pumpkin.api.mappack.MappackWorld;

import java.util.Collection;
import java.util.Date;

@DatabaseTable(tableName = "mappacks")
public class DatabaseMappack implements Mappack {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField(columnName = "public")
    private boolean isPublic;

    @DatabaseField
    private String version;

    @DatabaseField(columnName = "description_by", foreign = true)
    private DatabaseUser descriptionBy;

    @DatabaseField(columnName = "description_updated")
    private Date descriptionUpdated;

    @ForeignCollectionField
    private ForeignCollection<DatabaseMappackAuthor> authors;

    @ForeignCollectionField
    private ForeignCollection<DatabaseMappackTeam> teams;

    @ForeignCollectionField
    private ForeignCollection<DatabaseMappackWorld> worlds;

    @ForeignCollectionField
    private ForeignCollection<DatabaseMappackFile> files;

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
        //this is ugly, but java compiler is being stupid
        //noinspection unchecked
        return ((Collection) this.authors);
    }

    @Override
    public Collection<MappackWorld> getWorlds() {
        //this is ugly, but java compiler is being stupid
        //noinspection unchecked
        return ((Collection) this.worlds);
    }

    @Override
    public Collection<MappackFile> getFiles() {
        //this is ugly, but java compiler is being stupid
        //noinspection unchecked
        return ((Collection) this.files);
    }

    public Collection<DatabaseMappackTeam> getTeams() {
        return teams;
    }
}
