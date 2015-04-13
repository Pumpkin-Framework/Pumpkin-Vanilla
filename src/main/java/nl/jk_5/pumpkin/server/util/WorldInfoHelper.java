package nl.jk_5.pumpkin.server.util;

import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.api.mappack.MappackWorld;
import nl.jk_5.pumpkin.server.sql.SqlGamerules;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public final class WorldInfoHelper {

    private static final Logger logger = LogManager.getLogger();

    private WorldInfoHelper() {
    }

    public static void apply(WorldInfo info, @Nullable MappackWorld world){
        if(world == null){
            return;
        }

        info.saveVersion = 0x4abd;
        info.allowCommands = true;
        info.difficulty = EnumDifficulty.getDifficultyEnum(world.getDimension().getId());
        info.difficultyLocked = false;
        info.dimension = world.getDimension().getId();
        info.generatorOptions = world.getGeneratorOptions();
        info.hardcore = false;
        info.initialized = true;
        info.lastTimePlayed = 0;
        info.levelName = world.getMappack().getName() + "/" + world.getName();
        info.mapFeaturesEnabled = world.shouldGenerateStructures();
        info.playerTag = null;
        info.randomSeed = world.getSeed();
        info.sizeOnDisk = 0;
        info.spawnX = world.getSpawnpoint().getBlockX();
        info.spawnY = world.getSpawnpoint().getBlockY();
        info.spawnZ = world.getSpawnpoint().getBlockZ();
        info.theGameType = world.getGamemode();
        info.totalTime = 0;
        info.worldTime = world.getInitialTime();

        //TODO: worldborder in sql
        info.borderCenterX = 0;
        info.borderCenterZ = 0;
        info.borderSize = 6E7;
        info.borderSizeLerpTime = 0;
        info.borderSizeLerpTarget = 6E7;
        info.borderSafeZone = 5;
        info.borderDamagePerBlock = 0.2;
        info.borderWarningDistance = 5;
        info.borderWarningTime = 15;

        info.theGameRules = new SqlGamerules(world.getMappack());

        info.raining = false;
        info.thundering = false;
        info.rainTime = 100000;
        info.thunderTime = 100000;
        info.cleanWeatherTime = 0;

        String gen = world.getGenerator();
        if(gen.equals("overworld")){
            info.terrainType = WorldType.DEFAULT;
        }else if(gen.equals("nether")){
            info.terrainType = WorldType.DEFAULT;
        }else if(gen.equals("end")){
            info.terrainType = WorldType.DEFAULT;
        }else if(gen.equals("flat")){
            info.terrainType = WorldType.FLAT;
        }else if(gen.equals("void")){
            info.terrainType = WorldType.FLAT;
        }else if(gen.equals("large-biomes")){
            info.terrainType = WorldType.LARGE_BIOMES;
        }else if(gen.equals("amplified")){
            info.terrainType = WorldType.AMPLIFIED;
        }else{
            logger.warn("Unknown generator type " + gen);
        }
    }
}
