package nl.jk_5.pumpkin.server.lua;

/**
 * This interface has to be implemented by 'hosts' of machine instances.
 * <p/>
 * It provides some context for the machine, in particular which world it is
 * running in, to allow querying the time of day, for example.
 */
public interface MachineHost {

    Machine getMachine();

    String getScript();
}
