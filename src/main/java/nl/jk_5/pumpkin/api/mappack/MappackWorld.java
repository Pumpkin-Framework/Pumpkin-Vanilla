package nl.jk_5.pumpkin.api.mappack;

import net.minecraft.world.WorldSettings;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;
import nl.jk_5.pumpkin.server.util.location.Location;

@NonnullByDefault
public interface MappackWorld {

    int getId();

    Mappack getMappack();

    String getName();

    String getGenerator();

    Dimension getDimension();

    boolean isDefault();

    long getSeed();

    Location getSpawnpoint();

    WorldSettings.GameType getGamemode();

    boolean shouldGenerateStructures();

    int getInitialTime();

    String getGeneratorOptions();
}
