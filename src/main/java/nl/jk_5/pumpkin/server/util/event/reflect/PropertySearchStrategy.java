package nl.jk_5.pumpkin.server.util.event.reflect;

import com.google.common.collect.ImmutableSet;

/**
 * Finds all the properties in a class.
 */
public interface PropertySearchStrategy {

    /**
     * Enumerate a list of properties on a class, considering super types
     * and implemented interfaces.
     *
     * @param type The class
     * @return A set of properties
     */
    ImmutableSet<? extends Property> findProperties(final Class<?> type);

}
