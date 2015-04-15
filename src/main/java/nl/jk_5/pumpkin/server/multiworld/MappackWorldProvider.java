package nl.jk_5.pumpkin.server.multiworld;

import nl.jk_5.pumpkin.api.mappack.Dimension;
import nl.jk_5.pumpkin.api.mappack.MappackWorld;
import nl.jk_5.pumpkin.api.mappack.WorldProvider;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public class MappackWorldProvider implements WorldProvider {

    private final MappackWorld world;
    private int id;

    public MappackWorldProvider(MappackWorld world) {
        this.world = world;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Dimension getDimension() {
        return this.world.getDimension();
    }

    @Override
    public String getType() {
        return world.getGenerator();
    }

    @Override
    @Nullable
    public String getOptions() {
        if(world.getGenerator().equals("flat")){
            if(world.getGeneratorOptions().isEmpty()){
                return null;
            }
            return world.getGeneratorOptions();
        }else{
            return null;
        }
    }
}
