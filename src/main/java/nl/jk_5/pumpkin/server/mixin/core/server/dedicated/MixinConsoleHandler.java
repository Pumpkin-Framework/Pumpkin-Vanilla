package nl.jk_5.pumpkin.server.mixin.core.server.dedicated;

import jline.console.ConsoleReader;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.launch.console.VanillaConsole;
import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.util.ConsoleCommandCompleter;

import java.io.IOException;

@Mixin(targets = "net/minecraft/server/dedicated/DedicatedServer$2")
public abstract class MixinConsoleHandler extends Thread {

    @Shadow(remap = false, aliases = {"field_72428_a", "this$0"})
    private DedicatedServer server;

    @Override @Overwrite
    public void run() {
        final ConsoleReader reader = VanillaConsole.getReader();
        reader.addCompleter(new ConsoleCommandCompleter(this.server));

        try{
            Pumpkin.instance().consoleInitLatch.await();
        }catch(InterruptedException e){
            return;
        }

        String line;
        while(!this.server.isServerStopped() && this.server.isServerRunning()){
            try{
                line = reader.readLine("> ");

                if(line != null){
                    line = line.trim();
                    if(!line.isEmpty()){
                        this.server.addPendingCommand(line, this.server);
                    }
                }
            }catch(IOException e){
                LogManager.getLogger("Nailed").error("Exception handling console input", e);
            }
        }
    }
}
