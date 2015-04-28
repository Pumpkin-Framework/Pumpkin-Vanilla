package nl.jk_5.pumpkin.server.util;

import static com.google.common.base.Preconditions.checkNotNull;

import jline.console.completer.Completer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@NonnullByDefault
public class ConsoleCommandCompleter implements Completer {

    private final DedicatedServer server;

    public ConsoleCommandCompleter(DedicatedServer server) {
        this.server = checkNotNull(server, "server");
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        int len = buffer.length();
        buffer = buffer.trim();
        if (buffer.isEmpty()) {
            return cursor;
        }

        boolean prefix;
        if (buffer.charAt(0) != '/') {
            buffer = '/' + buffer;
            prefix = false;
        } else {
            prefix = true;
        }

        final String input = buffer;
        @SuppressWarnings("unchecked")
        Future<List<String>> tabComplete = this.server.callFromMainThread(new Callable<List<String>>() {

            @Override
            public List<String> call() throws Exception {
                return server.getTabCompletions(server, input, server.getPosition());
            }
        });

        try {
            List<String> completions = tabComplete.get();
            if (prefix) {
                candidates.addAll(completions);
            } else {
                for (String completion : completions) {
                    if (!completion.isEmpty()) {
                        candidates.add(completion.charAt(0) == '/' ? completion.substring(1) : completion);
                    }
                }
            }

            int pos = buffer.lastIndexOf(' ');
            if (pos == -1) {
                return cursor - len;
            } else {
                return cursor - (len - pos - 1);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LogManager.getLogger("Pumpkin").error("Failed to tab complete", e);
        }

        return cursor;
    }
}
