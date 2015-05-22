package nl.jk_5.pumpkin.server.lua;

import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.server.settings.Settings;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.Random;
import javax.annotation.Nullable;

@NonnullByDefault
public final class LuaStateFactory {

    private static final Logger logger = LogManager.getLogger();

    private LuaStateFactory() {
    }

    @Nullable
    public static LuaState create(){
        LuaState state;
        if(Settings.lua.disableMemoryLimit){
            state = new LuaState();
        }else{
            state = new LuaState(Integer.MAX_VALUE);
        }
        try{
            state.openLib(LuaState.Library.BASE);
            state.openLib(LuaState.Library.BIT32);
            state.openLib(LuaState.Library.COROUTINE);
            state.openLib(LuaState.Library.DEBUG);
            state.openLib(LuaState.Library.MATH);
            state.openLib(LuaState.Library.STRING);
            state.openLib(LuaState.Library.TABLE);
            state.pop(7);

            if(!Settings.lua.disableLocaleChanging){
                state.openLib(LuaState.Library.OS);
                state.getField(-1, "setlocale");
                state.pushString("C");
                state.call(1, 0);
                state.pop(1);
            }

            state.newTable();
            state.setGlobal("os");

            // Kill lua <--> java compat functions
            state.pushNil();
            state.setGlobal("unpack");
            state.pushNil();
            state.setGlobal("loadstring");
            state.getGlobal("math");
            state.pushNil();
            state.setField(-2, "log10");
            state.pop(1);
            state.getGlobal("table");
            state.pushNil();
            state.setField(-2, "maxn");
            state.pop(1);

            // Remove script loading functions that will be able to break out of the sandbox
            state.pushNil();
            state.setGlobal("dofile");
            state.pushNil();
            state.setGlobal("loadfile");

            state.getGlobal("math");

            // We give each Lua state it's own randomizer, since otherwise they'd
            // use the good old rand() from C. Which can be terrible, and isn't
            // necessarily thread-safe.
            final Random random = new Random();
            state.pushJavaFunction(new JavaFunction() {
                @Override
                public int invoke(LuaState lua) {
                    double r = random.nextDouble();
                    int top = lua.getTop();
                    if (top == 0) {
                        lua.pushNumber(r);
                    } else if (top == 1) {
                        double u = lua.checkNumber(1);
                        lua.checkArg(1, 1 <= u, "interval is empty");
                        lua.pushNumber(Math.floor(r * u) + 1);
                    } else if (top == 2) {
                        double l = lua.checkNumber(1);
                        double u = lua.checkNumber(2);
                        lua.checkArg(2, l <= u, "interval is empty");
                        lua.pushNumber(Math.floor(r * (u - l + 1)) + l);
                    } else {
                        throw new IllegalArgumentException("wrong number of arguments");
                    }
                    return 0;
                }
            });
            state.setField(-2, "random");

            state.pushJavaFunction(new JavaFunction() {
                @Override
                public int invoke(LuaState lua) {
                    random.setSeed((long) lua.checkNumber(1));
                    return 0;
                }
            });
            state.setField(-2, "randomseed");
            state.pop(1);

            return state;
        }catch(UnsatisfiedLinkError e){
            logger.error("Failed loading the native libraries");
        }catch(Throwable t){
            logger.warn("Failed creating Lua state", t);
        }
        return null;
    }
}
