package nl.jk_5.pumpkin.server.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

import nl.jk_5.pumpkin.api.Gamemode;
import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.UUID;
import javax.annotation.Nullable;

@NonnullByDefault
public class Player {

    private final UUID uuid;

    private GameProfile gameProfile;
    private boolean online = false;

    @Nullable private MapWorld world;
    @Nullable private EntityPlayerMP entity;
    @Nullable private NetHandlerPlayServer netHandler;
    @Nullable private Map map;
    @Nullable private User user;

    public Player(GameProfile profile) {
        this.gameProfile = profile;
        this.uuid = profile.getId();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return gameProfile.getName();
    }

    public EntityPlayerMP getEntity() {
        return entity;
    }

    public boolean isOnline() {
        return online;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    public void setEntity(@Nullable EntityPlayerMP entity) {
        this.entity = entity;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setWorld(@Nullable MapWorld world) {
        this.world = world;
    }

    public MapWorld getWorld() {
        return world;
    }

    @Nullable
    public Map getMap() {
        return map;
    }

    public void setMap(@Nullable Map map) {
        this.map = map;
    }

    public void setNetHandler(@Nullable NetHandlerPlayServer netHandler) {
        this.netHandler = netHandler;
    }

    public void setUser(@Nullable User user) {
        this.user = user;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public boolean isEditorMode(){
        return false; //TODO
    }

    public void setGamemode(Gamemode gm){
        getEntity().setGameType(WorldSettings.GameType.getByID(gm.getId()));
        IChatComponent comp = null;
        switch(getGamemode()){
            case SURVIVAL:
                comp = new ChatComponentText("Your game mode was changed to survival");
                break;
            case CREATIVE:
                comp = new ChatComponentText("Your game mode was changed to creative");
                break;
            case ADVENTURE:
                comp = new ChatComponentText("Your game mode was changed to adventure");
                break;
            case SPECTATOR:
                comp = new ChatComponentText("Your game mode was changed to spectator");
                break;
        }
        comp.getChatStyle().setColor(EnumChatFormatting.GREEN);
        getEntity().addChatMessage(comp);
    }

    public Gamemode getGamemode(){
        return Gamemode.getById(getEntity().theItemInWorldManager.getGameType().getID());
    }
}
