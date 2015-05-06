package nl.jk_5.pumpkin.api.event;

/**
 * Represents an event that can be cancelled.
 */
public interface Cancellable {

    /**
     * Gets if the {@link Event} has been cancelled.
     *
     * @return Is this event cancelled
     */
    boolean isCancelled();

    /**
     * Sets the cancelled state of the {@link Event}.
     *
     * <p>This will also cancel any callbacks on the event if {@code cancel}
     * is {@code true}. However, no callbacks will be un-cancelled if
     * {@code cancel} is {@code false}.</p>
     *
     * @param cancel The new cancelled state
     */
    void setCancelled(boolean cancel);
}
