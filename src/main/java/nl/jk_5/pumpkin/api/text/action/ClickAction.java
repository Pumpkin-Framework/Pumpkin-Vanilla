package nl.jk_5.pumpkin.api.text.action;

import java.net.URL;

/**
 * Represents a {@link TextAction} that responds to clicks.
 *
 * @param <R> The type of the result of the action
 */
public abstract class ClickAction<R> extends TextAction<R> {

    /**
     * Constructs a new {@link ClickAction} with the given result.
     *
     * @param result The result of the click action
     */
    protected ClickAction(R result) {
        super(result);
    }

    /**
     * Opens a url.
     */
    public static final class OpenUrl extends ClickAction<URL> {

        /**
         * Constructs a new {@link OpenUrl} instance that will ask the player to
         * open an URL when it is clicked.
         *
         * @param url The url to open
         */
        public OpenUrl(URL url) {
            super(url);
        }

    }

    /**
     * Runs a command.
     */
    public static final class RunCommand extends ClickAction<String> {

        /**
         * Constructs a new {@link RunCommand} instance that will run a command
         * on the client when it is clicked.
         *
         * @param command The command to execute
         */
        public RunCommand(String command) {
            super(command);
        }

    }

    /**
     * For books, changes pages.
     */
    public static final class ChangePage extends ClickAction<Integer> {

        /**
         * Constructs a new {@link ChangePage} instance that will change the
         * page in a book when it is clicked.
         *
         * @param page The book page to switch to
         */
        public ChangePage(int page) {
            super(page);
        }

    }

    /**
     * Suggests a command in the prompt.
     */
    public static final class SuggestCommand extends ClickAction<String> {

        /**
         * Constructs a new {@link SuggestCommand} instance that will suggest
         * the player a command when it is clicked.
         *
         * @param command The command to suggest
         */
        public SuggestCommand(String command) {
            super(command);
        }

    }

}
