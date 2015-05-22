package nl.jk_5.pumpkin.server.lua.architecture.luac;

import com.google.common.collect.ImmutableList;
import com.naef.jnlua.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.server.lua.LuaStateFactory;
import nl.jk_5.pumpkin.server.lua.Machine;
import nl.jk_5.pumpkin.server.lua.Signal;
import nl.jk_5.pumpkin.server.lua.architecture.Architecture;
import nl.jk_5.pumpkin.server.lua.architecture.ExecutionResult;
import nl.jk_5.pumpkin.server.lua.architecture.luac.api.*;
import nl.jk_5.pumpkin.server.settings.Settings;

import java.io.IOException;
import java.util.List;

@Architecture.Name("Lua")
public class NativeLuaArchitecture implements Architecture {

    private static final Logger logger = LogManager.getLogger();

    private final List<? extends NativeLuaApi> apis;
    private final Machine machine;

    private int kernelMemory = 0;

    LuaState lua;

    public NativeLuaArchitecture(Machine machine) {
        this.machine = machine;
        this.apis = ImmutableList.of(
                new OSApi(this),
                new ComputerApi(this),
                new BiosApi(this),
                new UnicodeApi(this),
                new SystemApi(this)
        );
    }

    @Override
    public boolean isInitialized() {
        return kernelMemory > 0;
    }

    @Override
    public void runSynchronized() {
        assert lua.getTop() == 2;
        assert lua.isThread(1);
        assert lua.isFunction(2);

        try{
            // Synchronized call protocol requires the called function to return
            // a table, which holds the results of the call, to be passed back
            // to the coroutine.yield() that triggered the call.
            lua.call(0, 1);
            lua.checkType(2, LuaType.TABLE);
        }catch(LuaMemoryAllocationException e){
            throw new java.lang.OutOfMemoryError("not enough memory");
        }
    }

    @Override
    public ExecutionResult runThreaded(boolean isSynchronizedReturn) {
        try{
            // The kernel thread will always be at stack index one.
            assert lua.isThread(1);

            // Resume the Lua state and remember the number of results we got
            int results;
            if(isSynchronizedReturn){
                assert lua.getTop() == 2;
                assert lua.isTable(2);
                results = lua.resume(1, 1);
            }else{
                if(kernelMemory == 0){
                    // We're doing the initialization run
                    if(lua.resume(1, 0) > 0){
                        // We expect to get nothing here. If we do get something, we had an error
                        results = 0;
                    }else{
                        // Run the garbage collector to get rid of stuff left behind after
                        // the initialization phase to get a good estimate of the base
                        // memory usage the kernel has (including libraries). We remember
                        // that size to grant user-space programs a fixed base amount of
                        // memory, regardless of the memory need of the underlying system
                        // (which may change across releases).
                        lua.gc(LuaState.GcAction.COLLECT, 0);
                        kernelMemory = Math.max(lua.getTotalMemory() - lua.getFreeMemory(), 1);

                        // Fake zero sleep to avoid stopping if there are no signals.
                        lua.pushInteger(0);
                        results = 1;
                    }
                }else{
                    Signal signal = machine.popSignal();
                    if(signal != null){
                        lua.pushString(signal.getName());
                        for(Object arg : signal.getArgs()){
                            LuaStateUtils.pushValue(lua, arg);
                        }
                        results = lua.resume(1, 1 + signal.getArgs().length);
                    }else{
                        results = lua.resume(1, 0);
                    }
                }
            }

            // Check if the kernel is still alive.
            if(lua.status(1) == LuaState.YIELD){
                // If we get one function it must be a wrapper for a synchronized
                // call. The protocol is that a closure is pushed that is then called
                // from the main server thread, and returns a table, which is in turn
                // passed to the originating coroutine.yield().
                if(results == 1 && lua.isFunction(2)){
                    return new ExecutionResult.SynchronizedCall();
                }
                // Check if we are shutting down, and if so if we're rebooting. This
                // is signalled by boolean values, where `false` means shut down,
                // `true` means reboot (i.e shutdown then start again).
                else if(results == 1 && lua.isBoolean(2)){
                    return new ExecutionResult.Shutdown(lua.toBoolean(2));
                }else{
                    // If we have a single number, that's how long we may wait before
                    // resuming the state again. Note that the sleep may be interrupted
                    // early if a signal arrives in the meantime. If we have something
                    // else we just process the next signal or wait for one.
                    int ticks = (results == 1 && lua.isNumber(2)) ? (int) (lua.toNumber(2) * 20) : Integer.MAX_VALUE;
                    lua.pop(results);
                    return new ExecutionResult.Sleep(ticks);
                }
            }else{ // The kernel thread returned. If it threw we'd be in the catch below.
                assert lua.isThread(1);
                // We're expecting the result of a pcall, if anything, so boolean + (result | string).
                if(!lua.isBoolean(2) || !(lua.isString(3) || lua.isNoneOrNil(3))){
                    logger.warn("Kernel returned unexpected results.");
                }
                // The pcall *should* never return normally... but check for it nonetheless.
                if(lua.toBoolean(2)){
                    logger.warn("Kernel stopped unexpectedly.");
                    return new ExecutionResult.Shutdown(false);
                }else{
                    if(!Settings.lua.disableMemoryLimit){
                        lua.setTotalMemory(Integer.MAX_VALUE);
                    }
                    String error;
                    if(lua.isJavaObjectRaw(3)){
                        error = lua.toJavaObjectRaw(3).toString();
                    }else{
                        error = lua.toString(3);
                    }
                    if(error != null){
                        return new ExecutionResult.Error(error);
                    }else{
                        return new ExecutionResult.Error("unknown error");
                    }
                }
            }
        }catch(LuaRuntimeException e){
            logger.warn("Kernel crashed. This is a bug!\n" + e.toString() + "\n\tat" + StringUtils.join(e.getLuaStackTrace(), "\n\tat"));
            return new ExecutionResult.Error("kernel panic: this is a bug, check your log file and report it");
        }catch(LuaGcMetamethodException e){
            if(e.getMessage() != null){
                return new ExecutionResult.Error("kernel panic:\n" + e.getMessage());
            }else{
                return new ExecutionResult.Error("kernel panic:\nerror in garbage collection metamethod");
            }
        }catch(LuaMemoryAllocationException e){
            return new ExecutionResult.Error("not enough memory");
        }catch(Error e){
            if(e.getMessage().equals("not enough memory")){
                return new ExecutionResult.Error("not enough memory");
            }else{
                throw e;
            }
        }
    }

    @Override
    public boolean initialize() {
        // Creates a new state with all base libraries loaded into it.
        // This means the state has much more power than it rightfully should have,
        // so we sandbox it a bit in the following.
        LuaState state = LuaStateFactory.create();
        if(state == null){
            lua = null;
            machine.crash("native libraries not available");
            return false;
        }
        lua = state;

        for(NativeLuaApi api : this.apis){
            api.initialize();
        }

        try{
            lua.load(Machine.class.getResourceAsStream("/assets/pumpkin/lua/machine.lua"), "=machine", "t");
        }catch(IOException e){
            machine.crash("could not load kernel script");
            return false;
        }
        lua.newThread(); // Left as the first value on the stack.

        return true;
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void close() {
        if(lua != null){
            if(!Settings.lua.disableMemoryLimit){
                lua.setTotalMemory(Integer.MAX_VALUE);
            }
            lua.close();
            lua = null;
        }
        kernelMemory = 0;
    }

    public Machine getMachine() {
        return machine;
    }

    public int getKernelMemory() {
        return kernelMemory;
    }
}
