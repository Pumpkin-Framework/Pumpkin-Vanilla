package nl.jk_5.pumpkin.server.lua.architecture.luac;

import com.naef.jnlua.LuaState;

import nl.jk_5.pumpkin.server.lua.architecture.ArchitectureApi;

public abstract class NativeLuaApi extends ArchitectureApi {

    private final NativeLuaArchitecture owner;

    public NativeLuaApi(NativeLuaArchitecture owner) {
        super(owner.getMachine());
        this.owner = owner;
    }

    protected LuaState lua() {
        return owner.lua;
    }

    public NativeLuaArchitecture getOwner() {
        return owner;
    }
}
