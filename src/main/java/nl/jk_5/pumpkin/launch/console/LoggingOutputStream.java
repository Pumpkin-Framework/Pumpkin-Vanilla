package nl.jk_5.pumpkin.launch.console;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@NonnullByDefault
public class LoggingOutputStream extends ByteArrayOutputStream {

    private static final String SEPARATOR = System.getProperty("line.separator");

    private final Logger logger;
    private final Level level;

    public LoggingOutputStream(Logger logger, Level level) {
        this.logger = checkNotNull(logger, "logger");
        this.level = checkNotNull(level, "level");
    }

    @Override
    public void flush() throws IOException {
        String message = toString();
        reset();

        if(!message.isEmpty() && !message.equals(SEPARATOR)){
            this.logger.log(this.level, message);
        }
    }
}
