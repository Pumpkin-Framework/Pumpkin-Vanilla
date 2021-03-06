package nl.jk_5.pumpkin.server.multiworld;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.*;

import nl.jk_5.pumpkin.api.mappack.WorldProvider;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.multiworld.gen.ChunkProviderVoid;
import nl.jk_5.pumpkin.server.multiworld.gen.WorldChunkManagerVoid;

public class DelegatingWorldProvider extends net.minecraft.world.WorldProvider {

    private final WorldProvider wrapped;

    private int dimId;
    private MapWorld world;
    private Map map;

    public DelegatingWorldProvider(WorldProvider wrapped){
        this.wrapped = wrapped;
    }

    @Override
    public String getDimensionName() {
        return "DIM" + wrapped.getId();
    }

    public void setWrappedDimension(int dimensionId) {
        this.dimId = dimensionId;
        wrapped.setId(dimensionId);
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        if(wrapped.getType().equals("overworld") || wrapped.getType().equals("large-biomes") || wrapped.getType().equals("amplified")){
            return new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), ""); //TODO: is the "" correct? Extra options
        }else if(wrapped.getType().equals("void")){
            return new ChunkProviderVoid(this.worldObj);
        }else if(wrapped.getType().equals("nether")){
            return new ChunkProviderHell(this.worldObj, false, this.worldObj.getSeed()); //True to generate nether fortresses
        }else if(wrapped.getType().equals("end")){
            return new ChunkProviderEnd(this.worldObj, this.worldObj.getSeed());
        }else if(wrapped.getType().equals("flat")){
            return new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.wrapped.getOptions());
        }else{
            throw new IllegalArgumentException("Unknown generator type " + this.wrapped.getType());
        }
    }

    @Override
    protected void registerWorldChunkManager() {
        if(wrapped.getType().equals("overworld") || wrapped.getType().equals("large-biomes") || wrapped.getType().equals("amplified")){
            this.worldChunkMgr = new WorldChunkManager(this.worldObj);
        }else if(wrapped.getType().equals("void")){
            this.worldChunkMgr = new WorldChunkManagerVoid(this.worldObj);
        }else if(wrapped.getType().equals("nether")){
            this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F);
            this.isHellWorld = true;
            this.hasNoSky = true;
        }else if(wrapped.getType().equals("end")){
            this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F);
            this.hasNoSky = true;
        }else if(wrapped.getType().equals("flat")){
            FlatGeneratorInfo info = FlatGeneratorInfo.createFlatGeneratorFromString(this.wrapped.getOptions());
            this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.getBiome(info.getBiome()), 0.5F);
        }else{
            throw new IllegalArgumentException("Unknown generator type " + this.wrapped.getType());
        }
    }

    @Override
    public String getInternalNameSuffix() {
        return "";
    }

    public MapWorld getWorld(){
        if(this.world == null){
            this.world = Pumpkin.instance().getDimensionManager().getWorld(this.dimId);
        }
        return this.world;
    }

    public Map getMap(){
        if(this.map == null){
            this.map = this.getWorld().getMap();
        }
        return this.map;
    }

    public WorldProvider getWrapped() {
        return wrapped;
    }
}
