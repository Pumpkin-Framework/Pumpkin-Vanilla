package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.jk_5.pumpkin.api.mappack.MappackWorld;
import nl.jk_5.pumpkin.server.util.Location;

@DatabaseTable(tableName = "mappack_world")
public class DatabaseMappackWorld implements MappackWorld {

    @DatabaseField(generatedId = true, unique = true)
    private int id;

    @DatabaseField(columnName = "map_id", foreign = true, foreignAutoRefresh = true)
    private DatabaseMappack map;

    @DatabaseField
    private String name;

    @DatabaseField
    private String generator;

    @DatabaseField
    private String dimension;

    @DatabaseField(columnName = "isdefault")
    private boolean isDefault;

    @DatabaseField(columnName = "spawn_x")
    private double spawnX;

    @DatabaseField(columnName = "spawn_y")
    private double spawnY;

    @DatabaseField(columnName = "spawn_z")
    private double spawnZ;

    @DatabaseField(columnName = "spawn_yaw")
    private float spawnYaw;

    @DatabaseField(columnName = "spawn_pitch")
    private float spawnPitch;

    public DatabaseMappackWorld() {
    }

    public int getId() {
        return id;
    }

    public DatabaseMappack getMappack() {
        return map;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getGenerator() {
        return generator;
    }

    @Override
    public String getDimension() {
        return dimension;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    public Location getSpawnpoint(){
        return Location.builder().setX(spawnX).setY(spawnY).setZ(spawnZ).setYaw(spawnYaw).setPitch(spawnPitch).build();
    }
}
