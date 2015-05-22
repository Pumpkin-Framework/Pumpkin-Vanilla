package nl.jk_5.pumpkin.server.lua.architecture.luac.api;

import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaState;

import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaApi;
import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaArchitecture;

public class BiosApi extends NativeLuaApi {

    public BiosApi(NativeLuaArchitecture owner) {
        super(owner);
    }

    @Override
    public void initialize() {
        lua().newTable();

        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                lua.pushString(getMachine().host().getScript());
                return 1;
            }
        });
        lua().setField(-2, "load");

        lua().setGlobal("bios");
    }
}
