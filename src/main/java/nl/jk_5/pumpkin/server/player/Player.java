package nl.jk_5.pumpkin.server.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldSettings;
import org.apache.commons.lang3.LocaleUtils;

import nl.jk_5.pumpkin.api.gamemode.GameMode;
import nl.jk_5.pumpkin.api.gamemode.GameModes;
import nl.jk_5.pumpkin.api.net.PlayerConnection;
import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.Texts;
import nl.jk_5.pumpkin.api.text.format.TextColors;
import nl.jk_5.pumpkin.api.user.User;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.text.PumpkinTexts;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.Locale;
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

    public void setGamemode(GameMode gm){
        Text msg = null;
        if(gm == GameModes.SURVIVAL){
            msg = Texts.of(TextColors.GREEN, "Your game mode was changed to survival"); //TODO: translation
            getEntity().setGameType(WorldSettings.GameType.SURVIVAL);
        }else if(gm == GameModes.CREATIVE){
            msg = Texts.of(TextColors.GREEN, "Your game mode was changed to creative"); //TODO: translation
            getEntity().setGameType(WorldSettings.GameType.CREATIVE);
        }else if(gm == GameModes.ADVENTURE){
            msg = Texts.of(TextColors.GREEN, "Your game mode was changed to adventure"); //TODO: translation
            getEntity().setGameType(WorldSettings.GameType.ADVENTURE);
        }else if(gm == GameModes.SPECTATOR){
            msg = Texts.of(TextColors.GREEN, "Your game mode was changed to spectator"); //TODO: translation
            getEntity().setGameType(WorldSettings.GameType.SPECTATOR);
        }
        this.sendMessage(msg);
    }

    public GameMode getGamemode(){
        WorldSettings.GameType type = getEntity().theItemInWorldManager.getGameType();
        if(type == WorldSettings.GameType.SURVIVAL){
            return GameModes.SURVIVAL;
        }else if(type == WorldSettings.GameType.CREATIVE){
            return GameModes.CREATIVE;
        }else if(type == WorldSettings.GameType.ADVENTURE){
            return GameModes.ADVENTURE;
        }else if(type == WorldSettings.GameType.SPECTATOR){
            return GameModes.SPECTATOR;
        }else{
            return GameModes.NOT_SET;
        }
    }

    @Nullable
    public PlayerConnection getPlayerConnection(){
        return ((PlayerConnection) this.netHandler);
    }

    public boolean hasPermission(String permission) {
        return Pumpkin.instance().getPermissionsHandler().hasPermission(this, permission);
    }

    public Locale getLocale(){
        return LocaleUtils.toLocale(this.entity.translator);
    }

    public void sendMessage(Text message) {
        this.entity.addChatMessage(PumpkinTexts.toComponent(message, this.getLocale()));
    }
}
