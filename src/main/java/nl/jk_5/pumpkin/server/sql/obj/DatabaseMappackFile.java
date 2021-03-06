package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.jk_5.pumpkin.api.mappack.MappackFile;

@DatabaseTable(tableName = "mappack_files")
public class DatabaseMappackFile implements MappackFile {

    @DatabaseField(generatedId = true, unique = true)
    private int id;

    @DatabaseField(columnName = "map_id", foreign = true, foreignAutoRefresh = true)
    private DatabaseMappack mappack;

    @DatabaseField
    private String path;

    @DatabaseField(columnName = "file_id")
    private String fileId;

    @DatabaseField(defaultValue = "TRUE")
    private boolean required;

    public int getId() {
        return id;
    }

    public DatabaseMappack getMappack() {
        return mappack;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getFileId() {
        return fileId;
    }

    @Override
    public boolean isRequired() {
        return required;
    }
}
