package nl.jk_5.pumpkin.server.mixin.core.server.dedicated;

import net.minecraft.server.dedicated.PropertyManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.server.settings.Settings;

import java.io.File;

@Mixin(PropertyManager.class)
public class MixinPropertyManager extends PropertyManager {

    @Shadow
    private static Logger LOGGER;

    public MixinPropertyManager(File file) {
        super(file);
    }

    private String remap(String key){
        if(key.equals("enable-query")){
            return "query.enabled";
        }else if(key.equals("enable-rcon")){
            return "rcon.enabled";
        }else if(key.equals("server-ip")){
            return "server.ip";
        }else if(key.equals("server-port")){
            return "server.port";
        }else{
            return key;
        }
    }

    @Overwrite
    public void generateNewProperties() {
    }

    @Overwrite
    public void saveProperties() {
    }

    @Overwrite
    public File getPropertiesFile() {
        return null;
    }

    @Overwrite
    public String getStringProperty(String key, String defaultValue) {
        key = remap(key);
        if(key == null) return defaultValue;
        if(!Settings.minecraft.hasPath(key)){
            LOGGER.warn("Config value for {} not found. Returning {}", key, defaultValue);
            return defaultValue;
        }
        return Settings.minecraft.getString(remap(key));
    }

    @Overwrite
    public int getIntProperty(String key, int defaultValue) {
        key = remap(key);
        if(key == null) return defaultValue;
        if(!Settings.minecraft.hasPath(key)){
            LOGGER.warn("Config value for {} not found. Returning {}", key, defaultValue);
            return defaultValue;
        }
        return Settings.minecraft.getInt(key);
    }

    @Overwrite
    public long getLongProperty(String key, long defaultValue) {
        key = remap(key);
        if(key == null) return defaultValue;
        if(!Settings.minecraft.hasPath(key)){
            LOGGER.warn("Config value for {} not found. Returning {}", key, defaultValue);
            return defaultValue;
        }
        return Settings.minecraft.getLong(key);
    }

    @Overwrite
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        key = remap(key);
        if(key == null) return defaultValue;
        if(!Settings.minecraft.hasPath(key)){
            LOGGER.warn("Config value for {} not found. Returning {}", key, defaultValue);
            return defaultValue;
        }
        return Settings.minecraft.getBoolean(key);
    }

    @Overwrite
    public void setProperty(String key, Object value) {
    }
}
