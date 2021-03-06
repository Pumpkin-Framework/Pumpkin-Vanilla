package nl.jk_5.pumpkin.launch.console;

import static com.google.common.base.Preconditions.checkState;
import static jline.TerminalFactory.JLINE_TERMINAL;
import static jline.TerminalFactory.OFF;
import static jline.console.ConsoleReader.RESET_LINE;

import com.mojang.util.QueueLogAppender;
import jline.console.ConsoleReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.fusesource.jansi.AnsiConsole;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.io.IOException;
import java.io.PrintStream;
import javax.annotation.Nullable;

@NonnullByDefault
public final class VanillaConsole {

    private VanillaConsole() {}

    @Nullable
    private static ConsoleReader reader;

    public static ConsoleReader getReader() {
        checkState(reader != null, "VanillaConsole was not initialized");
        return reader;
    }

    private static void disable() throws IOException {
        System.setProperty(JLINE_TERMINAL, OFF);
    }

    private static void initialize() throws IOException {
        reader = new ConsoleReader();
        reader.setExpandEvents(false);
    }

    public static void start() {
        if(reader != null) return;

        try {
            AnsiConsole.systemInstall();
            initialize();
        } catch (Exception e) {
            LogManager.getLogger("Pumpkin").error("Failed to initialize jline terminal. Falling back to default", e);

            try {
                disable();
                initialize();
            } catch (IOException e1) {
                throw new RuntimeException("Failed to initialize console", e1);
            }
        }

        System.setOut(new PrintStream(new LoggingOutputStream(LogManager.getLogger("SYSOUT"), Level.INFO), true));
        System.setErr(new PrintStream(new LoggingOutputStream(LogManager.getLogger("SYSERR"), Level.ERROR), true));

        Thread thread = new Thread(new Writer(), "Pumpkin Console Thread");
        thread.setDaemon(true);
        thread.start();
    }

    private static class Writer implements Runnable {

        @Override
        public void run() {
            String message;

            assert reader != null;

            //noinspection InfiniteLoopStatement
            while(true){
                message = QueueLogAppender.getNextLogEvent("VanillaConsole");
                if(message == null) {
                    continue;
                }

                try{
                    reader.print(RESET_LINE + message);
                    reader.drawLine();
                    reader.flush();
                }catch(IOException ignored){
                }
            }
        }
    }
}
