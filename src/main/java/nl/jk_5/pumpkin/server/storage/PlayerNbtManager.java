package nl.jk_5.pumpkin.server.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.IPlayerFileData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class PlayerNbtManager implements IPlayerFileData {

    private static final PlayerNbtManager INSTANCE = new PlayerNbtManager();
    private static final Logger logger = LogManager.getLogger();

    private final Map<UUID, NBTTagCompound> nbtCache = new HashMap<UUID, NBTTagCompound>();

    @Override
    public void writePlayerData(EntityPlayer player) {
        try {
            NBTTagCompound nbt = new NBTTagCompound();
            player.writeToNBT(nbt);
            this.nbtCache.put(player.getUniqueID(), nbt);
        }catch (Exception exception){
            logger.warn("Failed to save player data for " + player.getCommandSenderName());
        }
    }

    @Override
    public NBTTagCompound readPlayerData(EntityPlayer player) {
        NBTTagCompound nbt = null;

        try{
            nbt = this.nbtCache.get(player.getUniqueID());
        }catch (Exception exception){
            logger.warn("Failed to load player data for " + player.getCommandSenderName());
        }

        if(nbt != null){
            player.readFromNBT(nbt);
        }

        return nbt;
    }

    @Override
    public String[] getAvailablePlayerDat() {
        List<String> ret = new ArrayList<String>();
        for(UUID uuid : this.nbtCache.keySet()){
            ret.add(uuid.toString());
        }
        return ret.toArray(new String[ret.size()]);
    }

    public static PlayerNbtManager instance(){
        return INSTANCE;
    }
}
