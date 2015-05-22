package nl.jk_5.pumpkin.server.settings;

import com.typesafe.config.Config;

public class LuaVmSettings {

    public final boolean disableMemoryLimit;
    public final boolean disableLocaleChanging;
    public final int threadPriority;
    public final int threads;
    public final long executionDelay;
    public final boolean allowBytecode;
    public final double timeout;

    public LuaVmSettings(Config config) {
        disableMemoryLimit = config.getBoolean("disableMemoryLimit");
        disableLocaleChanging = config.getBoolean("disableLocaleChanging");
        threadPriority = config.getInt("threadPriority");
        threads = config.getInt("threads");
        executionDelay = config.getLong("executionDelay");
        allowBytecode = config.getBoolean("allowBytecode");
        timeout = config.getDouble("timeout");
    }
}
