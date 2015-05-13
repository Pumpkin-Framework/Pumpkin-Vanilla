package nl.jk_5.pumpkin.server.mixin.core.server.dedicated;

import jline.console.ConsoleReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import nl.jk_5.pumpkin.launch.console.VanillaConsole;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.util.ConsoleCommandCompleter;

import java.io.IOException;

@Mixin(targets = "net/minecraft/server/dedicated/DedicatedServer$2")
public abstract class MixinConsoleHandler extends Thread {

    @Override @Overwrite
    public void run() {
        final DedicatedServer server = ((DedicatedServer) MinecraftServer.getServer());
        final ConsoleReader reader = VanillaConsole.getReader();
        reader.addCompleter(new ConsoleCommandCompleter(server));

        try{
            Pumpkin.instance().consoleInitLatch.await();
        }catch(InterruptedException e){
            return;
        }

        String line;
        while(!server.isServerStopped() && server.isServerRunning()){
            try{
                line = reader.readLine("> ");

                if(line != null){
                    line = line.trim();
                    if(!line.isEmpty()){
                        server.addPendingCommand(line, server);
                    }
                }
            }catch(IOException ignored){
            }
        }
    }
}
