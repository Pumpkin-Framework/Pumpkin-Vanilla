package nl.jk_5.pumpkin.api.text.action;

/**
 * Represents a {@link TextAction} that responds to shift-clicks.
 *
 * @param <R> the type of the result of the action
 */
public abstract class ShiftClickAction<R> extends TextAction<R> {

    /**
     * Constructs a new {@link ShiftClickAction} with the given result.
     *
     * @param result The result of the shift click action
     */
    protected ShiftClickAction(R result) {
        super(result);
    }

    /**
     * Inserts some text into the chat prompt.
     */
    public static final class InsertText extends ShiftClickAction<String> {

        /**
         * Constructs a new {@link InsertText} instance that will insert text at
         * the current cursor position in the chat when it is shift-clicked.
         *
         * @param text The text to insert
         */
        public InsertText(String text) {
            super(text);
        }

    }
}