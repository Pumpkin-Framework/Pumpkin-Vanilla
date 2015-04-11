package nl.jk_5.pumpkin.server.util;

import net.minecraft.server.dedicated.DedicatedServer;

public class ShutdownThread extends Thread {

    private final DedicatedServer server;

    public ShutdownThread(DedicatedServer server) {
        super("Server Shutdown Thread");
        this.server = server;
    }

    @Override
    public void run() {
        server.stopServer();
    }
}
