package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import nl.jk_5.pumpkin.api.mappack.*;
import nl.jk_5.pumpkin.server.lua.Arguments;
import nl.jk_5.pumpkin.server.lua.Callback;
import nl.jk_5.pumpkin.server.lua.CallbackContainer;
import nl.jk_5.pumpkin.server.lua.Context;

import java.util.Collection;
import java.util.Date;

@DatabaseTable(tableName = "mappacks")
@SuppressWarnings("unused")
public class DatabaseMappack implements Mappack, CallbackContainer {

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

    @ForeignCollectionField
    private ForeignCollection<DatabaseGamerule> gamerules;

    @ForeignCollectionField
    private ForeignCollection<DatabaseZone> zones;

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

    @SuppressWarnings("unchecked")
    @Override
    public Collection<MappackAuthor> getAuthors() {
        return ((Collection) this.authors);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<MappackWorld> getWorlds() {
        return ((Collection) this.worlds);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<MappackFile> getFiles() {
        return ((Collection) this.files);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GameRule> getGameRules() {
        return ((Collection) this.gamerules);
    }

    @SuppressWarnings("unchecked")
    public Collection<DatabaseZone> getZones() {
        return this.zones;
    }

    public Collection<DatabaseMappackTeam> getTeams() {
        return teams;
    }

    @Callback(doc = "function():number -- Returns the id of this mappack")
    public Object[] id(Context context, Arguments arguments){
        return new Object[]{this.id};
    }

    @Callback(doc = "function():number -- Returns the id of this mappack")
    public Object[] yeah(Context context, Arguments arguments){
        System.out.println("IT WORKS. FUCK YES");
        return new Object[]{};
    }
}
