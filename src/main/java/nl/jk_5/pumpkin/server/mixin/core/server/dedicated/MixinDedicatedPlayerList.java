package nl.jk_5.pumpkin.server.mixin.core.server.dedicated;

import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DedicatedPlayerList.class)
public class MixinDedicatedPlayerList extends DedicatedPlayerList {

    @Shadow
    private static Logger LOGGER;

    private TObjectIntHashMap<String> userLevels = new TObjectIntHashMap<String>();

    private MixinDedicatedPlayerList(DedicatedServer server) {
        super(server);
    }

    @Overwrite
    private void readWhiteList(){
    }

    @Overwrite
    private void saveWhiteList(){
    }

    @Overwrite
    private void loadOpsList(){
    }

    @Overwrite
    private void saveOpsList(){
    }

    @Overwrite
    private void loadUserBansList(){
    }

    @Overwrite
    private void saveUserBanList(){
    }

    @Overwrite
    private void loadIpBanList(){
    }

    @Overwrite
    private void saveIpBanList(){
    }

    /*@Overwrite
    public boolean canJoin(GameProfile profile){
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
}
