package nl.jk_5.pumpkin.server.lua.architecture.luac.api;

import nl.jk_5.pumpkin.server.lua.architecture.luac.LuaStateUtils;
import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaApi;
import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaArchitecture;

public class MapApi extends NativeLuaApi {

    public MapApi(NativeLuaArchitecture owner) {
        super(owner);
    }

    @Override
    public void initialize() {
        LuaStateUtils.pushValue(lua(), getMachine().host(), getMachine());
        lua().setGlobal("map");
    }
}
