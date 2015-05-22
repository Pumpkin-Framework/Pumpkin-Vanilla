package nl.jk_5.pumpkin.server.lua.architecture.luac.api;

import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaType;

import nl.jk_5.pumpkin.server.lua.GameTimeFormatter;
import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaApi;
import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaArchitecture;

public class OSApi extends NativeLuaApi {

    public OSApi(NativeLuaArchitecture owner) {
        super(owner);
    }

    @Override
    public void initialize() {
        // Push a couple of functions that override original Lua API functions or
        // that add new functionality to it.
        lua().getGlobal("os");

        // Custom os.clock() implementation returning the time the computer has
        // been actively running, instead of the native library...
        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                lua.pushNumber(getMachine().cpuTime());
                return 1;
            }
        });
        lua().setField(-2, "clock");

        // Date formatting function.
        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                String format;
                if(lua.getTop() > 0 && lua.isString(1)){
                    format = lua.toString(1);
                }else{
                    format = "%d/%m/%y %H:%M:%S";
                }
                double time;
                if(lua.getTop() > 1 && lua.isNumber(2)){
                    time = lua.toNumber(2) * 1000 / 60 / 60;
                }else{
                    time = getMachine().worldTime() + 5000;
                }

                GameTimeFormatter.DateTime dt = GameTimeFormatter.parse(time);
                if(format.startsWith("!")) format = format.substring(1);

                if(format.equals("*t")){
                    lua.newTable(0, 8);
                    lua.pushInteger(dt.year);
                    lua.setField(-2, "year");
                    lua.pushInteger(dt.month);
                    lua.setField(-2, "month");
                    lua.pushInteger(dt.day);
                    lua.setField(-2, "day");
                    lua.pushInteger(dt.hour);
                    lua.setField(-2, "hour");
                    lua.pushInteger(dt.minute);
                    lua.setField(-2, "min");
                    lua.pushInteger(dt.second);
                    lua.setField(-2, "sec");
                    lua.pushInteger(dt.weekDay);
                    lua.setField(-2, "wday");
                    lua.pushInteger(dt.yearDay);
                    lua.setField(-2, "yday");
                }else{
                    lua.pushString(GameTimeFormatter.format(format, dt));
                }

                return 1;
            }
        });
        lua().setField(-2, "date");

        // Return ingame time for os.time().
        lua().pushJavaFunction(new JavaFunction() {
            @Override
            public int invoke(LuaState lua) {
                if(lua.isNoneOrNil(1)){
                    // Game time is in ticks, so that each day has 24000 ticks, meaning
                    // one hour is game time divided by one thousand. Also, Minecraft
                    // starts days at 6 o'clock, versus the 1 o'clock of timestamps so we
                    // add those five hours. Thus:
                    // timestamp = (time + 5000) * 60[kh] * 60[km] / 1000[s]
                    lua.pushNumber((getMachine().worldTime() + 5000) * 60 * 60 / 1000);
                }else{
                    lua.checkType(1, LuaType.TABLE);
                    lua.setTop(1);

                    int sec = getField(lua, "sec", 0);
                    int min = getField(lua, "min", 0);
                    int hour = getField(lua, "hour", 12);
                    int mday = getField(lua, "day", -1);
                    int mon = getField(lua, "month", -1);
                    int year = getField(lua, "year", -1);

                    int dt = GameTimeFormatter.mktime(year, mon, mday, hour, min, sec);
                    if(dt == -1){
                        lua.pushNil();
                    }else{
                        lua.pushNumber(dt);
                    }
                }
                return 1;
            }
        });
        lua().setField(-2, "time");

        // Pop the os table.
        lua().pop(1);
    }

    private int getField(LuaState lua, String key, int d) {
        lua.getField(-1, key);
        Integer res = lua.toIntegerX(-1);
        lua.pop(1);
        if(res == null){
            if(d < 0){
                throw new RuntimeException("field '" + key + "' missing in date table");
            }else{
                return d;
            }
        }else{
            return res;
        }
    }
}
