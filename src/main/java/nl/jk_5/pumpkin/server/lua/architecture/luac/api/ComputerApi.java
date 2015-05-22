package nl.jk_5.pumpkin.server.lua.architecture.luac.api;

import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaState;

import nl.jk_5.pumpkin.server.lua.architecture.luac.LuaStateUtils;
import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaApi;
import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaArchitecture;

public class ComputerApi extends NativeLuaApi {

    public ComputerApi(NativeLuaArchitecture owner) {
        super(owner);
    }

    @Override
    public void initialize() {
        // Computer API, stuff that kinda belongs to os, but we don't want to
        // clutter it.
        lua().newTable();

        // Allow getting the real world time for timeouts.
        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                lua.pushNumber(System.currentTimeMillis() / 1000.0);
                return 1;
            }
        });
        lua().setField(-2, "realTime");

        // The time the computer has been running, as opposed to the CPU time.
        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                lua.pushNumber(getMachine().upTime());
                return 1;
            }
        });
        lua().setField(-2, "uptime");

        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                lua.pushInteger(Math.min(lua.getFreeMemory(), (lua.getTotalMemory() - getOwner().getKernelMemory())));
                return 1;
            }
        });
        lua().setField(-2, "freeMemory");

        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                lua.pushInteger(lua.getTotalMemory() - getOwner().getKernelMemory());
                return 1;
            }
        });
        lua().setField(-2, "totalMemory");

        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                lua.pushBoolean(getMachine().signal(lua.checkString(1), LuaStateUtils.toSimpleJavaObjects(lua, 2)));
                return 1;
            }
        });
        lua().setField(-2, "pushSignal");

        // Set the computer table.
        lua().setGlobal("computer");
    }
}
