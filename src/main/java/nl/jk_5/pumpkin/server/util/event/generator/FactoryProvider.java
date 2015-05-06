package nl.jk_5.pumpkin.server.util.event.generator;

/**
 * Creates event factories that can generate new instances of requested
 * events.
 */
public interface FactoryProvider {

    /**
     * Get whether there should be any checks on whether a parameter is
     * null when it should not be.
     *
     * @return The null policy
     */
    NullPolicy getNullPolicy();

    /**
     * Set whether there should be any checks on whether a parameter is
     * null when it should not be.
     *
     * @param policy The null policy
     */
    void setNullPolicy(NullPolicy policy);

    /**
     * Creates a function that takes a map of property names with their
     * values to create a new instance of a generated class that implements
     * the given type.
     *
     * @param type The type to generate a class for
     * @param parentType The parent type
     * @param <T> The type of the event
     * @return The function
     */
    <T> EventFactory<T> create(Class<T> type, Class<?> parentType);

}
