package nl.jk_5.pumpkin.server.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleInputThread extends Thread {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void run(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        try{
            while(!MinecraftServer.getServer().isServerStopped() && MinecraftServer.getServer().isServerRunning() && (input = reader.readLine()) != null){
                ((DedicatedServer) MinecraftServer.getServer()).addPendingCommand(input, MinecraftServer.getServer());
            }
        }catch(IOException ioexception1){
            logger.error("Exception handling console input", ioexception1);
        }
    }
}
