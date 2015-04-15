package nl.jk_5.pumpkin.server.multiworld.teleport;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.Validate;

import nl.jk_5.pumpkin.api.mappack.Dimension;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.util.Location;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.Collection;

@NonnullByDefault
public class Teleporter {

    public static boolean teleportPlayer(EntityPlayer player, TeleportOptions opt){
        Validate.notNull(player, "player");
        Validate.notNull(opt, "opt");

        TeleportOptions options = opt.copy(); //We don't want to accidently modify the options object passed in, so we copy it.
        MapWorld currentWorld = Pumpkin.instance().getDimensionManager().getWorld(player.dimension);
        /*if(!TeleportEventFactory.isTeleportAllowed(currentWorld, opt.getDestination().getWorld(), player, options)){
            return false;
        }*/
        Location location = opt.getDestination(); //TeleportEventFactory.alterDestination(currentWorld, opt.getDestination().getWorld(), player, options);
        MapWorld destinationWorld = location.getWorld();

        teleportEntity(currentWorld, destinationWorld, player, location, options);
        return true;
    }

    public static Entity teleportEntity(MapWorld currentWorld, MapWorld destinationWorld, Entity entity, Location location, TeleportOptions options){
        WorldServer destWorld = destinationWorld.getWrapped();
        int dimension = destWorld.provider.getDimensionId();

        //Player player = null;
        EntityPlayerMP player = null;
        if(entity instanceof EntityPlayerMP){
            player = (EntityPlayerMP) entity;
        }

        /*if(player != null && !TeleportEventFactory.isTeleportAllowed(currentWorld, destinationWorld, player, options)){
            return entity;
        }*/
        Entity mount = entity.ridingEntity;
        if(entity.ridingEntity != null){
            entity.mountEntity(null);
            mount = teleportEntity(currentWorld, destinationWorld, mount, location, options);
        }
        boolean changingworlds = entity.worldObj != destWorld;
        /*if(player != null){
            TeleportEventFactory.onLinkStart(currentWorld, destinationWorld, player, options);
        }*/
        entity.worldObj.updateEntityWithOptionalForce(entity, false);
        if(entity instanceof EntityPlayerMP){
            EntityPlayerMP p = ((EntityPlayerMP) entity);
            p.closeScreen();
            if(changingworlds){
                Dimension oldDimension = currentWorld.getConfig().getDimension();
                Dimension newDimension = destinationWorld.getConfig().getDimension();

                p.dimension = dimension;
                if(oldDimension != newDimension){
                    p.playerNetServerHandler.sendPacket(new S07PacketRespawn(newDimension.getId(), destWorld.getDifficulty(), destWorld.getWorldInfo().getTerrainType(), p.theItemInWorldManager.getGameType()));
                }else{
                    if(newDimension == Dimension.END){
                        p.playerNetServerHandler.sendPacket(new S07PacketRespawn(-1, destWorld.getDifficulty(), destWorld.getWorldInfo().getTerrainType(), p.theItemInWorldManager.getGameType()));
                    }else{
                        p.playerNetServerHandler.sendPacket(new S07PacketRespawn(1, destWorld.getDifficulty(), destWorld.getWorldInfo().getTerrainType(), p.theItemInWorldManager.getGameType()));
                    }
                    p.playerNetServerHandler.sendPacket(new S07PacketRespawn(newDimension.getId(), destWorld.getDifficulty(), destWorld.getWorldInfo().getTerrainType(), p.theItemInWorldManager.getGameType()));
                }
                ((WorldServer) p.worldObj).getPlayerManager().removePlayer(p);
            }
        }
        if(changingworlds){
            removeEntityFromWorld(entity.worldObj, entity);
        }
        /*if(player != null){
            TeleportEventFactory.onExitWorld(currentWorld, destinationWorld, player, options);
        }*/

        entity.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        destWorld.theChunkProviderServer.loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
        if(changingworlds){
            if(!(entity instanceof EntityPlayerMP)){
                NBTTagCompound entityNBT = new NBTTagCompound();
                entity.isDead = false;
                entity.writeToNBTOptional(entityNBT);
                entity.isDead = true;
                entity = EntityList.createEntityFromNBT(entityNBT, destWorld);
                if(entity == null){
                    return null;
                }
                entity.dimension = destWorld.provider.getDimensionId();
            }
            destWorld.spawnEntityInWorld(entity);
            entity.setWorld(destWorld);
        }
        entity.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        /*if(player != null){
            TeleportEventFactory.onEnterWorld(currentWorld, destinationWorld, player, options);
        }*/
        destWorld.updateEntityWithOptionalForce(entity, false);
        entity.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        if(entity instanceof EntityPlayerMP){
            EntityPlayerMP p = ((EntityPlayerMP) entity);
            if(changingworlds){
                p.mcServer.getConfigurationManager().preparePlayer(p, destWorld);
            }
            p.playerNetServerHandler.setPlayerLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        }
        destWorld.updateEntityWithOptionalForce(entity, false);
        if(entity instanceof EntityPlayerMP){
            EntityPlayerMP p = ((EntityPlayerMP) entity);
            /*if(player != null){
                NailedPlayer pl = ((NailedPlayer) player);
                pl.world = location.getWorld();
                pl.map = pl.world.getMap();
            }*/
            p.theItemInWorldManager.setWorld(destWorld);
            p.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(p, destWorld);
            p.mcServer.getConfigurationManager().syncPlayerInventory(p);
            //noinspection unchecked
            for(PotionEffect effect : (Collection<PotionEffect>) p.getActivePotionEffects()){
                p.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(p.getEntityId(), effect));
            }
            p.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(p.experience, p.experienceTotal, p.experienceLevel));
        }
        entity.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        //if(player != null) TeleportEventFactory.onEnd(currentWorld, destinationWorld, player, options);
        if(mount != null){
            if(entity instanceof EntityPlayerMP){
                destWorld.updateEntityWithOptionalForce(entity, true);
            }
            entity.mountEntity(mount);
        }
        if(entity instanceof EntityLiving){
            EntityLiving e = ((EntityLiving) entity);
            e.setHealth(e.getMaxHealth());
            e.fallDistance = 0;
        }
        return entity;
    }

    public static void removeEntityFromWorld(World world, Entity entity){
        if(entity instanceof EntityPlayerMP){
            EntityPlayerMP p = ((EntityPlayerMP) entity);
            p.closeScreen();
            world.playerEntities.remove(p);
            world.updateAllPlayersSleepingFlag();
            int x = entity.chunkCoordX;
            int z = entity.chunkCoordZ;
            if(entity.addedToChunk && world.getChunkProvider().chunkExists(x, z)){
                Chunk chunk = world.getChunkFromChunkCoords(x, z);
                chunk.removeEntity(entity);
                chunk.setChunkModified();
            }
            world.loadedEntityList.remove(entity);
            world.onEntityRemoved(entity);
        }else{
            world.removeEntity(entity);
            world.onEntityRemoved(entity);
        }
    }
}
