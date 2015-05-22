package nl.jk_5.pumpkin.server.lua.architecture.luac.api;

import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaApi;
import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaArchitecture;
import nl.jk_5.pumpkin.server.settings.Settings;

public class SystemApi extends NativeLuaApi {

    private static final Logger logger = LogManager.getLogger();

    public SystemApi(NativeLuaArchitecture owner) {
        super(owner);
    }

    @Override
    public void initialize() {
        // Until we get to ingame screens we log to Java's stdout.
        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                String ret = "";
                for (int i = 1; i <= lua.getTop(); i++) {
                    LuaType type = lua.type(i);
                    switch (type) {
                        case NIL:
                            ret += "nil";
                            break;
                        case BOOLEAN:
                            ret += lua.toBoolean(i);
                            break;
                        case NUMBER:
                            ret += lua.toNumber(i);
                            break;
                        case STRING:
                            ret += lua.toString(i);
                            break;
                        case TABLE:
                            ret += "table";
                            break;
                        case FUNCTION:
                            ret += "function";
                            break;
                        case THREAD:
                            ret += "thread";
                            break;
                        case LIGHTUSERDATA:
                        case USERDATA:
                            ret += "userdata";
                            break;
                    }
                    ret += "  ";
                }
                logger.info(ret.trim());
                return 0;
            }
        });
        lua().setGlobal("print");

        // Create system table, avoid magic global non-tables.
        lua().newTable();

        // Whether bytecode may be loaded directly.
        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                lua.pushBoolean(Settings.lua.allowBytecode);
                return 1;
            }
        });
        lua().setField(-2, "allowBytecode");

        // How long programs may run without yielding before we stop them.
        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                lua.pushNumber(Settings.lua.timeout);
                return 1;
            }
        });
        lua().setField(-2, "timeout");

        lua().setGlobal("system");
    }
}
