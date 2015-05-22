package nl.jk_5.pumpkin.server.lua.architecture;

import nl.jk_5.pumpkin.server.lua.Machine;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
public abstract class ArchitectureApi {

    private final Machine machine;

    public ArchitectureApi(Machine machine) {
        this.machine = machine;
    }

    public Machine getMachine() {
        return machine;
    }

    public abstract void initialize();
}
