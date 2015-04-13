package nl.jk_5.pumpkin.server.multiworld.gen;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.WorldChunkManager;

import java.util.List;
import java.util.Random;

public class WorldChunkManagerVoid extends WorldChunkManager {

    private final World world;

    public WorldChunkManagerVoid(World world) {
        super(world);
        this.world = world;
    }

    @Override
    public BlockPos findBiomePosition(int x, int z, int range, List biomes, Random random) {
        BlockPos ret = super.findBiomePosition(x, z, range, biomes, random);
        if(x == 0 && z == 0 && !world.getWorldInfo().isInitialized()){
            if(ret == null){
                ret = BlockPos.ORIGIN;
            }
            BlockPos spawn = new BlockPos(0, 63, 0);
            if(world.isAirBlock(spawn)){
                world.setBlockState(spawn, Blocks.bedrock.getDefaultState());
            }
        }
        return ret;
    }
}
