package nl.jk_5.pumpkin.server.lua;

import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.lua.map.MachineImpl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class MachineEventHandler {

    private static final Set<MachineImpl> machines = new HashSet<MachineImpl>();

    private MachineEventHandler() {
    }

    public static void tick(){
        Iterator<MachineImpl> it = machines.iterator();
        while(it.hasNext()){
            MachineImpl machine = it.next();
            if(machine.tryClose()){
                it.remove();
            }
        }
        Pumpkin.instance().getMapLoader().tick();
    }

    public static void scheduleClose(MachineImpl machine) {
        machines.add(machine);
    }

    public static void unscheduleClose(MachineImpl machine) {
        machines.remove(machine);
    }
}
