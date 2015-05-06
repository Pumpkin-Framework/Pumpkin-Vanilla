package nl.jk_5.pumpkin.api.event;

/**
 * Manages the registration of event handlers and the dispatching of events.
 */
public interface EventManager {

    /**
     * Registers an object to receive {@link Event}s.
     *
     * @param obj The object
     * @throws IllegalArgumentException Thrown if {@code plugin} is not a plugin instance
     */
    void register(Object obj);

    /**
     * Un-registers an object from receiving {@link Event}s.
     *
     * @param obj The object
     */
    void unregister(Object obj);

    /**
     * Calls a {@link Event} to all handlers that handle it.
     *
     * @param event The event
     * @return True if canceled, false if not
     */
    boolean post(Event event);

}
