package nl.jk_5.pumpkin.server.mixin.core.server.management;

import net.minecraft.server.management.ServerConfigurationManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager {

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
}
