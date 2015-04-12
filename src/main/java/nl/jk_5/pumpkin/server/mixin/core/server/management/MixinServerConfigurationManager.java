package nl.jk_5.pumpkin.server.mixin.core.server.management;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.event.player.PlayerJoinServerEvent;
import nl.jk_5.pumpkin.server.event.player.PlayerRespawnEvent;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.multiworld.DelegatingWorldProvider;
import nl.jk_5.pumpkin.server.multiworld.DimensionManagerImpl;
import nl.jk_5.pumpkin.server.storage.PlayerNbtManager;
import nl.jk_5.pumpkin.server.util.Location;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(ServerConfigurationManager.class)
public abstract class MixinServerConfigurationManager {

    @Shadow private static Logger logger;
    @Shadow private MinecraftServer mcServer;
    @Shadow private Map<UUID, StatisticsFile> playerStatFiles;
    @Shadow private List<EntityPlayerMP> playerEntityList;
    @Shadow private Map<UUID, EntityPlayerMP> uuidToPlayerMap;

    //@Shadow public abstract NBTTagCompound readPlayerDataFromFile(EntityPlayerMP playerIn);
    @Shadow public abstract void setPlayerGameTypeBasedOnOther(EntityPlayerMP p_72381_1_, EntityPlayerMP p_72381_2_, World worldIn);
    @Shadow public abstract void func_96456_a(ServerScoreboard scoreboardIn, EntityPlayerMP playerIn);
    @Shadow public abstract int getMaxPlayers();
    @Shadow public abstract void sendChatMsg(IChatComponent component);
    @Shadow public abstract void playerLoggedIn(EntityPlayerMP playerIn);
    @Shadow public abstract void updateTimeAndWeatherForPlayer(EntityPlayerMP playerIn, WorldServer worldIn);

    /*@Overwrite
    public String allowUserToConnect(SocketAddress address, GameProfile profile) {
        Connection conn = null;
        try{
            conn = Pumpkin.instance().getSqlService().getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ops WHERE userid=?");
            stmt.setString(1, profile.getId().toString());
            ResultSet rs = stmt.executeQuery();
            if(rs.first()){
                //Player is op
                userLevels.put(profile.getId().toString(), rs.getInt("level"));
                stmt.close();
                return true;
            }else{
                //Player is not op
                userLevels.put(profile.getId().toString(), 0);
                stmt.close();
            }

        } catch (SQLException e) {
            LOGGER.error("Was not able to check if player {} may join the game. Not letting him in", profile);
        } finally{
            SqlUtils.close(conn);
        }
        if(this.isWhiteListEnabled()){
            //TODO: check whitelist
        }

        return true; //TODO
    }*/

    @Overwrite
    public void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP player){
        PlayerProfileCache profileCache = this.mcServer.getPlayerProfileCache();
        GameProfile profile = player.getGameProfile();
        GameProfile oldProfile = profileCache.getProfileByUUID(profile.getId()); //Pull the profile from the cache
        String oldUsername;
        if(oldProfile == null){ //Was the profile found in the cache? Use that name. The player might have renamed itself
            oldUsername = profile.getName();
        }else{
            oldUsername = oldProfile.getName();
        }
        profileCache.addEntry(profile); //Save the new name into the cache

        MapWorld previousWorld = DimensionManagerImpl.instance().getWorld(player.dimension);

        NBTTagCompound playerData = this.readPlayerDataFromFile(player);

        boolean firstJoin = playerData == null;

        Location worldSpawnPoint = previousWorld.getConfig().getSpawnpoint().setWorld(previousWorld);
        Location previousLocation = firstJoin ? null : Location.builder().setWorld(previousWorld).setX(player.posX).setY(player.posY).setZ(player.posZ).setYaw(player.rotationYaw).setPitch(player.rotationPitch).build();

        IChatComponent joinMessage;
        if(!player.getCommandSenderName().equalsIgnoreCase(oldUsername)) {
            joinMessage = new ChatComponentTranslation("multiplayer.player.joined.renamed", player.getDisplayName(), oldUsername);
        }else{
            joinMessage = new ChatComponentTranslation("multiplayer.player.joined", player.getDisplayName());
        }
        joinMessage.getChatStyle().setColor(EnumChatFormatting.YELLOW);

        PlayerJoinServerEvent.Pre event = Pumpkin.instance().postEvent(new PlayerJoinServerEvent.Pre(player, netManager.getRemoteAddress(), joinMessage, worldSpawnPoint, previousLocation));
        if(event.getKickMessage() != null){
            netManager.sendPacket(new S00PacketDisconnect(event.getKickMessage()));
            netManager.closeChannel(event.getKickMessage());
            return;
        }
        Location spawnLocation = event.getLocation() == null ? event.getSpawnPoint() : event.getLocation();
        joinMessage = event.getJoinMessage();
        MapWorld spawnWorld = spawnLocation.getWorld();

        player.setWorld(spawnWorld.getWrapped());
        player.theItemInWorldManager.setWorld((WorldServer) player.worldObj);

        String playerAddress = "local";
        if(netManager.getRemoteAddress() != null){
            playerAddress = netManager.getRemoteAddress().toString();
        }
        logger.info(player.getCommandSenderName() + "[" + playerAddress + "] logged in with entity id " + player.getEntityId() + " at (" + player.posX + ", " + player.posY + ", " + player.posZ + ")");

        WorldInfo worldInfo = spawnWorld.getWrapped().getWorldInfo();

        this.setPlayerGameTypeBasedOnOther(player, null, spawnWorld.getWrapped());

        int dimid = (spawnWorld.getWrapped().provider instanceof DelegatingWorldProvider) ? ((DelegatingWorldProvider) spawnWorld.getWrapped().provider).getWrapped().getDimension().getId() : 0;
        NetHandlerPlayServer networkHandler = new NetHandlerPlayServer(this.mcServer, netManager, player);
        networkHandler.sendPacket(new S01PacketJoinGame(player.getEntityId(), player.theItemInWorldManager.getGameType(), worldInfo.isHardcoreModeEnabled(), dimid, spawnWorld.getWrapped().getDifficulty(), this.getMaxPlayers(), worldInfo.getTerrainType(), spawnWorld.getWrapped().getGameRules().getGameRuleBooleanValue("reducedDebugInfo")));
        networkHandler.sendPacket(new S3FPacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(this.mcServer.getServerModName())));
        networkHandler.sendPacket(new S41PacketServerDifficulty(worldInfo.getDifficulty(), worldInfo.isDifficultyLocked()));
        networkHandler.sendPacket(new S05PacketSpawnPosition(spawnLocation.toBlockPos()));
        networkHandler.sendPacket(new S39PacketPlayerAbilities(player.capabilities));
        networkHandler.sendPacket(new S09PacketHeldItemChange(player.inventory.currentItem));

        //Send achievements and stats
        player.getStatFile().func_150877_d();
        player.getStatFile().func_150884_b(player);

        //Set scoreboard stuff
        this.func_96456_a((ServerScoreboard) spawnWorld.getWrapped().getScoreboard(), player);

        //Update the status list (in the multiplayer window)
        this.mcServer.refreshStatusNextTick();

        //Send join message
        if(joinMessage != null){
            this.sendChatMsg(joinMessage);
        }

        this.playerLoggedIn(player);
        networkHandler.setPlayerLocation(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), spawnLocation.getYaw(), spawnLocation.getPitch());
        this.updateTimeAndWeatherForPlayer(player, spawnWorld.getWrapped());

        //Send resourcepack
        if(this.mcServer.getResourcePackUrl().length() > 0){
            player.loadResourcePack(this.mcServer.getResourcePackUrl(), this.mcServer.getResourcePackHash());
        }

        //Send potion effects
        //noinspection unchecked
        for(PotionEffect effect : (Collection<PotionEffect>) player.getActivePotionEffects()){
            networkHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), effect));
        }

        player.addSelfToInternalCraftingInventory();

        //Send mounted entities
        if(playerData != null && playerData.hasKey("Riding", 10)){
            Entity entity = EntityList.createEntityFromNBT(playerData.getCompoundTag("Riding"), spawnWorld.getWrapped());

            if (entity != null){
                entity.forceSpawn = true;
                spawnWorld.getWrapped().spawnEntityInWorld(entity);
                player.mountEntity(entity);
                entity.forceSpawn = false;
            }
        }

        Pumpkin.instance().postEvent(new PlayerJoinServerEvent.Post(player));
    }

    @Overwrite
    public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP player, int dimension, boolean conqueredEnd){
        MapWorld oldWorld = DimensionManagerImpl.instance().getWorld(player.dimension);
        player.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(player); //Tell the clients to destroy the old entity
        player.getServerForPlayer().getEntityTracker().untrackEntity(player); //Untrack player
        player.getServerForPlayer().getPlayerManager().removePlayer(player); //Remove player from the chunk loaders
        this.playerEntityList.remove(player); //Remove the player from the entity list
        oldWorld.getWrapped().removePlayerEntityDangerously(player);

        Location deathLocation = Location.builder().fromBlockPos(player.getPosition()).setWorld(oldWorld).setYaw(player.rotationYaw).setPitch(player.rotationPitch).build();
        BlockPos bedLocation = player.getBedLocation();
        boolean respawnAtBed = player.isSpawnForced();
        Location respawnLocation = oldWorld.getConfig().getSpawnpoint().setWorld(oldWorld); //Respawn at world spawn

        Location bedRespawnLocation = null;
        boolean bedObstructed = false;
        if(bedLocation != null){
            BlockPos safeBedSpawn = EntityPlayer.getBedSpawnLocation(oldWorld.getWrapped(), bedLocation, respawnAtBed);
            if(safeBedSpawn == null){
                bedObstructed = true;
            }else{
                respawnLocation = Location.builder().fromBlockPos(safeBedSpawn).setWorld(oldWorld).build().add(0.5, 0.1, 0.5);
                bedRespawnLocation = Location.builder().fromBlockPos(bedLocation).setWorld(oldWorld).build();
            }
        }

        PlayerRespawnEvent.Pre event = Pumpkin.instance().postEvent(new PlayerRespawnEvent.Pre(player, deathLocation, respawnLocation));

        if(event.getRespawnLocation() != respawnLocation){
            bedObstructed = false;
        }

        respawnLocation = event.getRespawnLocation();
        MapWorld respawnWorld = respawnLocation.getWorld();
        WorldServer newWorld = respawnWorld.getWrapped();

        player.dimension = newWorld.provider.getDimensionId();

        ItemInWorldManager interactionManager = new ItemInWorldManager(respawnWorld.getWrapped());

        EntityPlayerMP newPlayer = new EntityPlayerMP(this.mcServer, newWorld, player.getGameProfile(), interactionManager);
        newPlayer.playerNetServerHandler = player.playerNetServerHandler;
        newPlayer.clonePlayer(player, conqueredEnd);
        newPlayer.setEntityId(player.getEntityId());
        newPlayer.func_174817_o(player);

        this.setPlayerGameTypeBasedOnOther(newPlayer, player, newWorld);

        if(bedObstructed){
            newPlayer.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
        }
        newPlayer.setLocationAndAngles(respawnLocation.getX(), respawnLocation.getY(), respawnLocation.getZ(), respawnLocation.getYaw(), respawnLocation.getPitch());
        if(bedRespawnLocation != null){
            newPlayer.setSpawnPoint(bedRespawnLocation.toBlockPos(), respawnAtBed);
        }

        newWorld.theChunkProviderServer.loadChunk((int) newPlayer.posX >> 4, (int) newPlayer.posZ >> 4);

        //Removed collision check from here to allow for more cool things

        int dimid = (respawnWorld.getWrapped().provider instanceof DelegatingWorldProvider) ? ((DelegatingWorldProvider) respawnWorld.getWrapped().provider).getWrapped().getDimension().getId() : 0;
        newPlayer.playerNetServerHandler.sendPacket(new S07PacketRespawn(dimid, newPlayer.worldObj.getDifficulty(), newPlayer.worldObj.getWorldInfo().getTerrainType(), newPlayer.theItemInWorldManager.getGameType()));
        newPlayer.playerNetServerHandler.setPlayerLocation(newPlayer.posX, newPlayer.posY, newPlayer.posZ, newPlayer.rotationYaw, newPlayer.rotationPitch);
        newPlayer.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(respawnWorld.getConfig().getSpawnpoint().toBlockPos()));
        newPlayer.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(newPlayer.experience, newPlayer.experienceTotal, newPlayer.experienceLevel));

        this.updateTimeAndWeatherForPlayer(newPlayer, newWorld);
        newWorld.getPlayerManager().addPlayer(newPlayer);
        newWorld.spawnEntityInWorld(newPlayer);
        this.playerEntityList.add(newPlayer);
        this.uuidToPlayerMap.put(newPlayer.getUniqueID(), newPlayer);
        newPlayer.addSelfToInternalCraftingInventory();
        newPlayer.setHealth(newPlayer.getHealth());

        Pumpkin.instance().postEvent(new PlayerRespawnEvent.Post(newPlayer));

        return newPlayer;
    }

    @Overwrite
    public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP player){
        return PlayerNbtManager.instance().readPlayerData(player);
    }

    @Overwrite
    protected void writePlayerData(EntityPlayerMP playerIn){
        PlayerNbtManager.instance().writePlayerData(playerIn);

        StatisticsFile statisticsfile = this.playerStatFiles.get(playerIn.getUniqueID());
        if(statisticsfile != null){
            statisticsfile.saveStatFile();
        }
    }

    @Overwrite
    public String[] getAvailablePlayerDat() {
        return PlayerNbtManager.instance().getAvailablePlayerDat();
    }
}
