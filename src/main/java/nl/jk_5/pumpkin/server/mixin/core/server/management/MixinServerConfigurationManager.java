package nl.jk_5.pumpkin.server.mixin.core.server.management;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.event.PumpkinEventFactory;
import nl.jk_5.pumpkin.api.event.player.PlayerPreJoinServerEvent;
import nl.jk_5.pumpkin.api.event.player.PlayerPreRespawnEvent;
import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.Texts;
import nl.jk_5.pumpkin.api.text.format.TextColors;
import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.multiworld.DelegatingWorldProvider;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.storage.PlayerNbtManager;
import nl.jk_5.pumpkin.server.text.PumpkinTexts;
import nl.jk_5.pumpkin.server.util.location.Location;

import java.net.InetSocketAddress;
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
    @Shadow public abstract void sendScoreboard(ServerScoreboard scoreboardIn, EntityPlayerMP playerIn);
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

        MapWorld previousWorld = Pumpkin.instance().getDimensionManager().getWorld(player.dimension);

        NBTTagCompound playerData = this.readPlayerDataFromFile(player);

        boolean firstJoin = playerData == null;

        Player playerObj = Pumpkin.instance().getPlayerManager().getOrCreatePlayer(player);
        playerObj.setEntity(player);
        playerObj.setOnline(true);

        Location worldSpawnPoint = previousWorld.getConfig().getSpawnpoint().setWorld(previousWorld);
        Location previousLocation = firstJoin ? null : Location.builder().setWorld(previousWorld).setX(player.posX).setY(player.posY).setZ(player.posZ).setYaw(player.rotationYaw).setPitch(player.rotationPitch).build();

        Text joinMessage;
        if(!player.getCommandSenderName().equalsIgnoreCase(oldUsername)) {
            joinMessage = Texts.of(TextColors.YELLOW, Pumpkin.instance().getRegistry().getTranslationById("multiplayer.player.joined.renamed").get(), player.getDisplayName(), oldUsername);
        }else{
            joinMessage = Texts.of(TextColors.YELLOW, Pumpkin.instance().getRegistry().getTranslationById("multiplayer.player.joined").get(), player.getDisplayName());
        }

        PlayerPreJoinServerEvent event = PumpkinEventFactory.createPlayerPreJoinServerEvent(playerObj, ((InetSocketAddress) netManager.getRemoteAddress()), joinMessage, worldSpawnPoint, previousLocation);
        Pumpkin.instance().postEvent(event);
        if(event.getKickMessage() != null){
            netManager.sendPacket(new S00PacketDisconnect(PumpkinTexts.toComponent(event.getKickMessage(), playerObj.getLocale())));
            netManager.closeChannel(PumpkinTexts.toComponent(event.getKickMessage(), playerObj.getLocale()));
            return;
        }
        Location spawnLocation = event.getLocation() == null ? event.getSpawnPoint() : event.getLocation();
        joinMessage = event.getJoinMessage();
        MapWorld spawnWorld = spawnLocation.getWorld();

        player.setWorld(spawnWorld.getWrapped());
        player.theItemInWorldManager.setWorld((WorldServer) player.worldObj);
        playerObj.setWorld(spawnWorld);
        playerObj.setMap(spawnWorld.getMap());
        playerObj.setNetHandler(player.playerNetServerHandler);
        playerObj.getWorld().onPlayerJoined(playerObj);
        if(playerObj.getMap() != null){
            playerObj.getMap().onPlayerJoined(playerObj);
        }

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



        User user = Pumpkin.instance().getUserManager().getByMojangId(playerObj.getUuid());
        if(user != null){
            event.getPlayer().setUser(user);

            IChatComponent comp = new ChatComponentText("Welcome back, " + user.getUsername());
            comp.getChatStyle().setColor(EnumChatFormatting.GREEN);
            event.getPlayer().getEntity().addChatMessage(comp);

            S47PacketPlayerListHeaderFooter packet = new S47PacketPlayerListHeaderFooter();
            packet.header = new ChatComponentText("Pumpkin");
            packet.header.getChatStyle().setColor(EnumChatFormatting.AQUA);
            packet.footer = new ChatComponentText("See your stats on ");
            packet.footer.getChatStyle().setColor(EnumChatFormatting.AQUA);
            IChatComponent comp2 = new ChatComponentText("https://pumpkin.jk-5.nl/#/user/" + user.getUsername());
            comp2.getChatStyle().setColor(EnumChatFormatting.GOLD);
            comp2.getChatStyle().setUnderlined(true);
            packet.footer.appendSibling(comp2);

            networkHandler.sendPacket(packet);
        }else{
            IChatComponent comp = new ChatComponentText("Hello " + event.getPlayer().getName() + ". Welcome to pumpkin");
            comp.getChatStyle().setColor(EnumChatFormatting.GOLD);
            event.getPlayer().getEntity().addChatMessage(comp);

            comp = new ChatComponentText("Your minecraft account is not linked to a pumpkin account");
            comp.getChatStyle().setColor(EnumChatFormatting.GOLD);
            event.getPlayer().getEntity().addChatMessage(comp);

            comp = new ChatComponentText("That means that we can not track your statistics");
            comp.getChatStyle().setColor(EnumChatFormatting.GOLD);
            event.getPlayer().getEntity().addChatMessage(comp);

            comp = new ChatComponentText("If you don't have a pumpkin account yet, go to");
            comp.getChatStyle().setColor(EnumChatFormatting.GOLD);
            event.getPlayer().getEntity().addChatMessage(comp);

            comp = new ChatComponentText("");
            IChatComponent c2 = new ChatComponentText("https://pumpkin.jk-5.nl/#/account/new");
            c2.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to go to the site")));
            c2.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://pumpkin.jk-5.nl/#/account/new"));
            c2.getChatStyle().setColor(EnumChatFormatting.GREEN);
            c2.getChatStyle().setUnderlined(true);
            comp.appendSibling(c2);
            c2 = new ChatComponentText(" to create one");
            c2.getChatStyle().setColor(EnumChatFormatting.GOLD);
            comp.appendSibling(c2);
            event.getPlayer().getEntity().addChatMessage(comp);

            comp = new ChatComponentText("If you already have one, or when you are done registering, do");
            comp.getChatStyle().setColor(EnumChatFormatting.GOLD);
            event.getPlayer().getEntity().addChatMessage(comp);

            comp = new ChatComponentText("/login <username> <password>");
            comp.getChatStyle().setColor(EnumChatFormatting.GREEN);
            comp.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to log in")));
            comp.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/login "));
            event.getPlayer().getEntity().addChatMessage(comp);

            S47PacketPlayerListHeaderFooter packet = new S47PacketPlayerListHeaderFooter();
            packet.header = new ChatComponentText("Pumpkin");
            packet.header.getChatStyle().setColor(EnumChatFormatting.AQUA);
            packet.footer = new ChatComponentText("Do /login or create an account on ");
            packet.footer.getChatStyle().setColor(EnumChatFormatting.AQUA);
            IChatComponent comp2 = new ChatComponentText("https://pumpkin.jk-5.nl/#/account/new");
            comp2.getChatStyle().setColor(EnumChatFormatting.GOLD);
            comp2.getChatStyle().setUnderlined(true);
            packet.footer.appendSibling(comp2);

            networkHandler.sendPacket(packet);
        }



        //Send achievements and stats
        player.getStatFile().func_150877_d();
        player.getStatFile().sendAchievements(player);

        //Set scoreboard stuff
        this.sendScoreboard((ServerScoreboard) spawnWorld.getWrapped().getScoreboard(), player);

        //Update the status list (in the multiplayer window)
        this.mcServer.refreshStatusNextTick();

        //Send join message
        //TODO: nice broadcast api for this
        for(Player p : Pumpkin.instance().getPlayerManager().getOnlinePlayers()){
            p.sendMessage(joinMessage);
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

        Pumpkin.instance().postEvent(PumpkinEventFactory.createPlayerPostJoinServerEvent(playerObj));
        if(playerObj.getMap() != null){
            Pumpkin.instance().postEvent(PumpkinEventFactory.createPlayerJoinMapEvent(playerObj.getMap(), playerObj));
        }
        Pumpkin.instance().postEvent(PumpkinEventFactory.createPlayerJoinWorldEvent(playerObj.getWorld(), playerObj));
    }

    @Overwrite
    public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP player, int dimension, boolean conqueredEnd){
        MapWorld oldWorld = Pumpkin.instance().getDimensionManager().getWorld(player.dimension);
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

        Player playerObj = Pumpkin.instance().getPlayerManager().getFromEntity(player);
        PlayerPreRespawnEvent event = PumpkinEventFactory.createPlayerPreRespawnEvent(playerObj, deathLocation, respawnLocation);
        Pumpkin.instance().postEvent(event);

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

        playerObj.setEntity(newPlayer);

        if(respawnWorld != oldWorld){
            if(playerObj.getMap() != null){
                Pumpkin.instance().postEvent(PumpkinEventFactory.createPlayerLeaveMapEvent(playerObj.getMap(), playerObj));
            }
            Pumpkin.instance().postEvent(PumpkinEventFactory.createPlayerLeaveWorldEvent(playerObj.getWorld(), playerObj));

            playerObj.getWorld().onPlayerLeft(playerObj);
            if(playerObj.getMap() != null){
                playerObj.getMap().onPlayerLeft(playerObj);
            }

            playerObj.setWorld(respawnWorld);
            playerObj.setMap(respawnWorld.getMap());

            playerObj.getWorld().onPlayerJoined(playerObj);
            if(playerObj.getMap() != null){
                playerObj.getMap().onPlayerJoined(playerObj);
            }

            Pumpkin.instance().postEvent(PumpkinEventFactory.createPlayerJoinWorldEvent(playerObj.getWorld(), playerObj));
            if(playerObj.getMap() != null){
                Pumpkin.instance().postEvent(PumpkinEventFactory.createPlayerJoinMapEvent(playerObj.getMap(), playerObj));
            }
        }

        Pumpkin.instance().postEvent(PumpkinEventFactory.createPlayerPostRespawnEvent(playerObj));

        return newPlayer;
    }

    @Overwrite
    public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP player){
        return PlayerNbtManager.instance().readPlayerData(player);
    }

    @Overwrite
    protected void writePlayerData(EntityPlayerMP playerIn){
        PlayerNbtManager.instance().writePlayerData(playerIn);

        MapWorld world = Pumpkin.instance().getDimensionManager().getWorld(playerIn.dimension);

        //assert world.getMap() != null;
        /*StatisticsFile statsFile = world.getMap().getPlayerStats().get(playerIn.getUniqueID());
        if(statsFile != null){
            statsFile.saveStatFile();
        }*/
    }

    @Overwrite
    public String[] getAvailablePlayerDat() {
        return PlayerNbtManager.instance().getAvailablePlayerDat();
    }

    /*@Overwrite
    public StatisticsFile getPlayerStatsFile(EntityPlayer playerEntity){
        Player player = Pumpkin.instance().getPlayerManager().getFromEntity(((EntityPlayerMP) playerEntity));
        //noinspection ConstantConditions
        if(player == null){
            File statsDir = new File("stats");
            statsDir.mkdirs();
            return new StatisticsFile(MinecraftServer.getServer(), new File(statsDir, playerEntity.getUniqueID().toString() + ".json"));
        }
        MapWorld world = Pumpkin.instance().getDimensionManager().getWorld(((EntityPlayerMP) playerEntity).dimension);
        nl.jk_5.pumpkin.server.mappack.Map map = world.getMap();
        //assert map != null;
        UUID uuid = player.getUuid();
        StatisticsFile statsFile = map.getPlayerStats().get(uuid);

        if(statsFile == null){
            File statsDir = new File(map.getDir(), "stats");
            statsDir.mkdir();
            File statsStorage = new File(statsDir, uuid.toString() + ".json");

            statsFile = new StatisticsFile(this.mcServer, statsStorage);
            statsFile.readStatFile();
            map.getPlayerStats().put(uuid, statsFile);
        }

        return statsFile;
    }

    @Inject(method = "playerLoggedOut(Lnet/minecraft/entity/player/EntityPlayerMP;)V", at = @At("RETURN"))
    public void playerLoggedOut(EntityPlayerMP playerIn, CallbackInfo info){
        MapWorld world = Pumpkin.instance().getDimensionManager().getWorld(playerIn.dimension);
        //assert world.getMap() != null;
        world.getMap().getPlayerStats().remove(playerIn.getUniqueID());
    }*/
}
