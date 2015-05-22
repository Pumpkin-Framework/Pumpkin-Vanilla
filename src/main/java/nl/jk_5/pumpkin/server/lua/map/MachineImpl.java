package nl.jk_5.pumpkin.server.lua.map;

import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.server.lua.*;
import nl.jk_5.pumpkin.server.lua.architecture.Architecture;
import nl.jk_5.pumpkin.server.lua.architecture.ExecutionResult;
import nl.jk_5.pumpkin.server.lua.architecture.luac.NativeLuaArchitecture;
import nl.jk_5.pumpkin.server.settings.Settings;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public class MachineImpl implements Machine, Runnable {

    private static final Logger logger = LogManager.getLogger();
    private static final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Settings.lua.threads, new ThreadFactory() {

        private final String baseName = "Pumpkin-Machine-";
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadGroup group = System.getSecurityManager() == null ? Thread.currentThread().getThreadGroup() : System.getSecurityManager().getThreadGroup();

        @Override
        public Thread newThread(@Nonnull Runnable r) {
            Thread thread = new Thread(group, r, baseName + threadNumber.getAndIncrement());
            if (!thread.isDaemon()) {
                thread.setDaemon(true);
            }
            if (thread.getPriority() != Settings.lua.threadPriority) {
                thread.setPriority(Settings.lua.threadPriority);
            }
            return thread;
        }
    });

    private final MachineHost host;
    private final Architecture architecture;
    private final Stack<State> state;
    private final Queue<SimpleSignal> signals = new LinkedList<SimpleSignal>();

    private String lastErrorMessage = null;

    private long uptime = 0L; // Game-world time [ticks] for os.uptime().
    private long cpuTotal = 0L; // Pseudo-real-world time [ns] for os.clock().
    private long cpuStart = 0L; // Pseudo-real-world time [ns] for os.clock().
    private int remainIdle = 0; // Ticks left to sleep before resuming.
    private int remainingPause = 0; // Ticks left to wait before resuming.
    private volatile double callBudget = 0.0;
    private boolean inSynchronizedCall = false; // We want to ignore the call limit in synchronized calls to avoid errors.

    public MachineImpl(MachineHost host) {
        this.host = host;

        this.state = new Stack<State>();
        this.state.push(State.STOPPED);

        this.architecture = new NativeLuaArchitecture(this);
        this.architecture.onConnect();
    }

    @Override
    public MachineHost host() {
        return host;
    }

    @Override
    public void onHostChanged() {

    }

    @Override
    public Architecture architecture() {
        return architecture;
    }

    @Override
    public String tmpAddress() {
        return null;
    }

    @Override
    public String lastError() {
        return this.lastErrorMessage;
    }

    @Override
    public double upTime() {
        // World time is in ticks, and each second has 20 ticks. Since we
        // want uptime() to return real seconds, though, we'll divide it
        // accordingly.
        return uptime / 20.0;
    }

    @Override
    public double cpuTime() {
        return (cpuTotal + (System.nanoTime() - cpuStart)) * 10e-10;
    }

    @Override
    public boolean isRunning() {
        synchronized (state){
            return state.firstElement() != State.STOPPED && state.firstElement() != State.STOPPING;
        }
    }

    @Override
    public boolean isPaused() {
        synchronized (state){
            return state.firstElement() == State.PAUSED && remainingPause > 0;
        }
    }

    @Override
    public boolean start() {
        synchronized (state){
            State top = state.firstElement();
            switch (top){
                case STOPPED:
                    onHostChanged();
                    if(architecture == null){
                        crash("gui.Error.NoCPU");
                        return false;
                    }else if(!init()){
                        return false;
                    }else{
                        switchTo(State.STARTING);
                        uptime = 0;
                        //node.sendToReachable("computer.started");
                        return true;
                    }
                case PAUSED:
                    if(remainingPause > 0){
                        remainingPause = 0;
                        return true;
                    }
                    break;
                case STOPPING:
                    switchTo(State.RESTARTING);
                    MachineEventHandler.unscheduleClose(this);
                    return true;
                default: return false;
            }
        }
        return false;
    }

    @Override
    public boolean pause(double seconds) {
        int ticksToPause = Math.max((int) (seconds * 20), 0);
        synchronized (state){
            if(state.firstElement() == State.STOPPED || state.firstElement() == State.STOPPED){
                return false;
            }
            if(state.firstElement() == State.PAUSED && ticksToPause <= remainingPause){
                return false;
            }
        }
        synchronized (this){
            synchronized (state){
                if(state.firstElement() == State.STOPPED || state.firstElement() == State.STOPPED){
                    return false;
                }
                if(state.firstElement() == State.PAUSED && ticksToPause <= remainingPause){
                    return false;
                }
                if(state.firstElement() != State.PAUSED){
                    assert !state.contains(State.PAUSED);
                    state.push(State.PAUSED);
                }
                remainingPause = ticksToPause;
                return true;
            }
        }
    }

    @Override
    public boolean stop() {
        synchronized (state){
            State s = state.firstElement();
            if(s == State.STOPPED || s == State.STOPPING){
                return false;
            }else{
                state.push(State.STOPPING);
                MachineEventHandler.scheduleClose(this);
                return true;
            }
        }
    }

    @Override
    public boolean crash(String message) {
        this.lastErrorMessage = message;
        synchronized (state){
            boolean result = stop();
            if(state.firstElement() == State.STOPPING){
                // When crashing, make sure there's no "Running" left in the stack.
                state.clear();
                state.push(State.STOPPING);
            }
            logger.error("Machine crash: " + message);
            return result;
        }
    }

    @Override
    public boolean signal(String name, Object... args) {
        synchronized (state){
            State s = state.firstElement();
            if(s == State.STOPPED || s == State.STOPPING) {
                return false;
            }else{
                synchronized (signals){
                    if(signals.size() > 256){
                        return false;
                    }else if(args == null || args.length == 0){
                        signals.offer(new SimpleSignal(name, ArrayUtils.EMPTY_OBJECT_ARRAY));
                        return true;
                    }else{
                        List<Object> a = new ArrayList<Object>();
                        for(Object arg : args){
                            if(arg == null){
                                a.add(null);
                            }else if(arg instanceof Boolean){
                                a.add(arg);
                            }else if(arg instanceof Byte){
                                a.add(((Byte) arg).doubleValue());
                            }else if(arg instanceof Character){
                                //noinspection UnnecessaryBoxing
                                a.add(Double.valueOf(((Character) arg).charValue()));
                            }else if(arg instanceof Short){
                                a.add(((Short) arg).doubleValue());
                            }else if(arg instanceof Integer){
                                a.add(((Integer) arg).doubleValue());
                            }else if(arg instanceof Long){
                                a.add(((Long) arg).doubleValue());
                            }else if(arg instanceof Float){
                                a.add(((Float) arg).doubleValue());
                            }else if(arg instanceof Double){
                                a.add(arg);
                            }else if(arg instanceof String){
                                a.add(arg);
                            }else if(arg instanceof byte[]){
                                a.add(arg);
                            }else if(arg instanceof java.util.Map<?, ?> && !((java.util.Map) arg).isEmpty() && ((java.util.Map) arg).keySet().iterator().next() instanceof String && ((java.util.Map) arg).values().iterator().next() instanceof String){
                                a.add(arg);
                            }else if(arg instanceof NBTTagCompound){
                                a.add(arg);
                            }else{
                                logger.warn("Trying to push signal with an unsupported argument of type " + arg.getClass().getName());
                                a.add(null);
                            }
                        }
                        SimpleSignal sig = new SimpleSignal(name, a.toArray(new Object[a.size()]));
                        signals.offer(sig);
                        return true;
                    }
                }
            }
        }
    }

    @Override
    public Signal popSignal() {
        synchronized (signals){
            if(signals.isEmpty()){
                return null;
            }else{
                return signals.poll().convert();
            }
        }
    }

    @Override
    public java.util.Map<String, Callback> methods(Object value) {
        java.util.Map<String, Callbacks.Callback> cbs = Callbacks.search(value);
        java.util.Map<String, Callback> ret = new HashMap<String, Callback>();
        for(java.util.Map.Entry<String, Callbacks.Callback> entry : cbs.entrySet()){
            ret.put(entry.getKey(), entry.getValue().getAnnotation());
        }
        return ret;
    }

    @Override
    public Object[] invoke(Value value, String method, Object[] args) throws Exception {
        Callbacks.Callback cb = Callbacks.search(value).get(method);
        if(cb == null){
            throw new NoSuchMethodException();
        }
        boolean direct = cb.getAnnotation().direct();
        if(direct && architecture.isInitialized()){
            checkLimit(cb.getAnnotation().limit());
        }
        return Registry.convert(cb.apply(value, this, new ArgumentsImpl(args)));
    }

    @Override
    public Object[] invoke(String address, String method, Object[] args) throws Exception {
        /*if(node != null && node.network != null) Option(node.network.node(address)) match {
            case Some(component: Component) if component.canBeSeenFrom(node) || component == node =>
                val direct = component.annotation(method).direct
                if (direct && architecture.isInitialized) {
                    checkLimit(component.annotation(method).limit)
                }
                component.invoke(method, this, args: _*)
            case _ => throw new IllegalArgumentException("no such component")
        }
        else {
            // Not really, but makes the VM stop, which is what we want in this case,
            // because it means we've been disconnected / disposed already.
            throw new LimitReachedException()
        }*/
        return new Object[0];
    }

    private void checkLimit(int limit) throws LimitReachedException {
        if(!inSynchronizedCall){
            double callCost = Math.max(1.0 / limit, 0.001);
            if(callCost >= callBudget){
                throw new LimitReachedException();
            }
            callBudget -= callCost;
        }
    }

    ////////////////////////////////////////////////////

    @Callback(doc = "function():boolean -- Starts the computer. Returns true if the state changed.")
    public Object[] start(Context context, Arguments args){
        return new Object[]{!isPaused() && start()};
    }

    @Callback(doc = "function():boolean -- Stops the computer. Returns true if the state changed.")
    public Object[] stop(Context context, Arguments args){
        return new Object[]{stop()};
    }

    @Callback(direct = true, doc = "function():boolean -- Returns whether the computer is running.")
    public Object[] isRunning(Context context, Arguments args){
        return new Object[]{isRunning()};
    }

    ////////////////////////////////////////////////////

    public boolean isExecuting(){
        synchronized (state){
            return state.contains(State.RUNNING);
        }
    }

    public void update(){
        synchronized (state){
            if(state.firstElement() == State.STOPPED){
                return;
            }
        }

        // Update world time for time() and uptime().
        //worldTime = host.world.getWorldTime();
        uptime += 1;

        if(remainIdle > 0){
            remainIdle -= 1;
        }

        // Reset direct call budget.
        callBudget = 1.0;

        // Check if we should switch states. These are all the states in which we're
        // guaranteed that the executor thread isn't running anymore.
        synchronized (state){
            State top = state.firstElement();
            switch (top){
                case STARTING: // Booting up
                    switchTo(State.YIELDED);
                    break;
                case RESTARTING: // Restarting
                    close();
                    //if (Settings.get.eraseTmpOnReboot) {
                    //    tmp.foreach(_.node.remove()) // To force deleting contents.
                    //    tmp.foreach(tmp => node.connect(tmp.node))
                    //}
                    //node.sendToReachable("computer.stopped")
                    start();
                    break;
                case SLEEPING:
                    if(remainIdle <= 0 || !signals.isEmpty()){ // Resume from pauses based on sleep or signal overflow
                        switchTo(State.YIELDED);
                    }
                    break;
                case PAUSED: // Resume in case we paused because the game was paused
                    if(remainingPause > 0){
                        remainingPause --;
                    }else{
                        state.pop();
                        switchTo(state.firstElement());
                    }
                    break;
                case SYNC_CALL: // Perform a synchronized call (message sending)
                    switchTo(State.RUNNING);
                    try{
                        inSynchronizedCall = true;
                        architecture.runSynchronized();
                        inSynchronizedCall = false;
                        // Check if the callback called pause() or stop().
                        switch (state.firstElement()){
                            case RUNNING:
                                switchTo(State.SYNC_RETURN);
                                break;
                            case PAUSED:
                                state.pop(); //Paused
                                state.pop(); //Running, no switchTo to avoid new future
                                state.push(State.SYNC_RETURN);
                                state.push(State.PAUSED);
                                break;
                            case STOPPING:
                                state.clear();
                                state.push(State.STOPPING);
                                break;
                            default: throw new AssertionError();
                        }
                    }catch(Error e){
                        if(e.getMessage().equals("not enough memory")){
                            crash("gui.error.OutOfMemory");
                        }else{
                            logger.warn("Faulty architecture implementation for synchronized calls.", e);
                            crash("gui.error.InternalError");
                        }
                    }catch(Throwable e){
                        logger.warn("Faulty architecture implementation for synchronized calls.", e);
                        crash("gui.error.InternalError");
                    }finally{
                        inSynchronizedCall = false;
                    }
                    assert !isExecuting();
                    break;
                default:
                    break;
            }
        }

        // Finally check if we should stop the computer. We cannot lock the state
        // because we may have to wait for the executor thread to finish, which
        // might turn into a deadlock depending on where it currently is.
        State top;
        synchronized (state){
            top = state.firstElement();
        }
        switch (top){
            case STOPPING:
                synchronized (this){
                    synchronized (state){
                        close();
                        //tmp.foreach(_.node.remove()) // To force deleting contents.
                        //if (node.network != null) {
                        //    tmp.foreach(tmp => node.connect(tmp.node))
                        //}
                        //node.sendToReachable("computer.stopped")
                    }
                }
                break;
            default:
                break;
        }
    }

    ////////////////////////////////////////////////////

    private boolean init(){
        onHostChanged();
        if(architecture == null){
            return false;
        }

        // Reset error state.
        lastErrorMessage = null;

        // Clear any left-over signals from a previous run.
        signals.clear();

        // Connect the `/tmp` node to our owner. We're not in a network in
        // case we're loading, which is why we have to check it here.
        //if (node.network != null) {
        //    tmp.foreach(fs => node.connect(fs.node))
        //}

        try{
            return architecture.initialize();
        }catch(Throwable e){
            logger.warn("Failed initializing computer.", e);
            close();
        }
        return false;
    }

    public boolean tryClose(){
        if(isExecuting()){
            return false;
        }else{
            close();
            return true;
        }
    }

    private void close(){
        synchronized (state){
            if(state.size() == 0 || state.firstElement() != State.STOPPED){
                synchronized (this){
                    state.clear();
                    state.push(State.STOPPED);
                    if(architecture != null){
                        architecture.close();
                    }
                    signals.clear();
                    uptime = 0;
                    cpuTotal = 0;
                    cpuStart = 0;
                    remainIdle = 0;
                }
            }
        }
    }

    ////////////////////////////////////////////////////

    private State switchTo(State value){
        State result = state.pop();
        if(value == State.STOPPING || value == State.RESTARTING) {
            state.clear();
        }
        state.push(value);
        if(value == State.YIELDED || value == State.SYNC_RETURN){
            remainIdle = 0;
            threadPool.schedule(this, Settings.lua.executionDelay, TimeUnit.MILLISECONDS);
        }

        return result;
    }

    @Override
    public void run() {
        synchronized (this){
            boolean isSynchronizedReturn;
            synchronized (state){
                if(state.firstElement() != State.YIELDED && state.firstElement() != State.SYNC_RETURN){
                    return;
                }
                isSynchronizedReturn = switchTo(State.RUNNING) == State.SYNC_RETURN;
            }

            cpuStart = System.nanoTime();

            try {
                ExecutionResult result = architecture.runThreaded(isSynchronizedReturn);

                // Check if someone called pause() or stop() in the meantime.
                synchronized(state) {
                    State top = state.firstElement();
                    switch(top){
                        case RUNNING:
                            if(result instanceof ExecutionResult.Sleep){
                                synchronized (signals){
                                    // Immediately check for signals to allow processing more than one
                                    // signal per game tick.
                                    if(signals.isEmpty() && ((ExecutionResult.Sleep) result).ticks > 0){
                                        switchTo(State.SLEEPING);
                                        remainIdle = ((ExecutionResult.Sleep) result).ticks;
                                    }else{
                                        switchTo(State.YIELDED);
                                    }
                                }
                            }else if(result instanceof ExecutionResult.SynchronizedCall){
                                switchTo(State.SYNC_CALL);
                            }else if(result instanceof ExecutionResult.Shutdown){
                                if(((ExecutionResult.Shutdown) result).reboot){
                                    switchTo(State.RESTARTING);
                                }else{
                                    switchTo(State.STOPPING);
                                }
                            }else if(result instanceof ExecutionResult.Error){
                                String msg = ((ExecutionResult.Error) result).message;
                                if(msg == null){
                                    msg = "unknown error";
                                }
                                crash(msg);
                            }
                            break;
                        case PAUSED:
                            state.pop(); //Paused
                            state.pop(); //Running, no switchTo to avoid new future
                            if(result instanceof ExecutionResult.Sleep){
                                remainIdle = ((ExecutionResult.Sleep) result).ticks;
                                state.push(State.SLEEPING);
                            }else if(result instanceof ExecutionResult.SynchronizedCall){
                                state.push(State.SYNC_CALL);
                            }else if(result instanceof ExecutionResult.Shutdown){
                                if(((ExecutionResult.Shutdown) result).reboot){
                                    state.push(State.RESTARTING);
                                }else{
                                    state.push(State.STOPPING);
                                }
                            }else if(result instanceof ExecutionResult.Error){
                                String msg = ((ExecutionResult.Error) result).message;
                                if(msg == null){
                                    msg = "unknown error";
                                }
                                crash(msg);
                            }
                            state.push(State.PAUSED);
                            break;
                        case STOPPING:
                            state.clear();
                            state.push(State.STOPPING);
                            break;
                        default:
                            throw new AssertionError("Invalid state in executor post-processing");
                    }
                    assert !isExecuting();
                }
            }catch(Throwable e){
                logger.warn("Architecture's runThreaded threw an error. This should never happen!", e);
                crash("gui.Error.InternalError");
            }

            // Keep track of time spent executing the computer.
            cpuTotal += System.nanoTime() - cpuStart;
        }
    }

    public enum State {
        STOPPED,
        STARTING,
        RESTARTING,
        STOPPING,
        PAUSED,
        SYNC_CALL,
        SYNC_RETURN,
        YIELDED,
        SLEEPING,
        RUNNING
    }

    public static class SimpleSignal implements Signal {

        private final String name;
        private final Object[] args;

        public SimpleSignal(String name, Object... args) {
            this.name = name;
            this.args = args;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Object[] getArgs() {
            return new Object[0];
        }

        public SimpleSignal convert(){
            return new SimpleSignal(name, Registry.convert(args));
        }
    }
}
