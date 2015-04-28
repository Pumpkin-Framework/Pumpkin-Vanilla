package nl.jk_5.pumpkin.server.sql.obj;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import net.minecraft.world.WorldSettings;

import nl.jk_5.pumpkin.api.mappack.Dimension;
import nl.jk_5.pumpkin.api.mappack.MappackWorld;
import nl.jk_5.pumpkin.server.util.location.Location;

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

    @DatabaseField(width = 32, defaultValue = "0")
    private String seed;

    @DatabaseField(width = 16, defaultValue = "'adventure'")
    private String gamemode;

    @DatabaseField(columnName = "generate_structures", defaultValue = "TRUE")
    private boolean generateStructures;

    @DatabaseField(columnName = "initial_time", defaultValue = "0")
    private int initialTime;

    @DatabaseField(columnName = "flat_generator_settings", canBeNull = true, width = 256)
    private String generatorOptions;

    @ForeignCollectionField
    private ForeignCollection<DatabaseZone> zones;

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
    public Dimension getDimension() {
        return Dimension.parse(dimension);
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public long getSeed() {
        try{
            return Long.parseLong(seed);
        }catch(NumberFormatException e){
            return seed.hashCode();
        }
    }

    @Override
    public Location getSpawnpoint(){
        return Location.builder().setX(spawnX).setY(spawnY).setZ(spawnZ).setYaw(spawnYaw).setPitch(spawnPitch).build();
    }

    @Override
    public WorldSettings.GameType getGamemode() {
        WorldSettings.GameType type = WorldSettings.GameType.getByName(gamemode);
        if(type == WorldSettings.GameType.NOT_SET) return WorldSettings.GameType.ADVENTURE;
        return type;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return generateStructures;
    }

    @Override
    public int getInitialTime() {
        return initialTime;
    }

    @Override
    public String getGeneratorOptions() {
        return generatorOptions;
    }

    public ForeignCollection<DatabaseZone> getZones() {
        return zones;
    }
}
