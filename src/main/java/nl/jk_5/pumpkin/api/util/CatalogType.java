package nl.jk_5.pumpkin.api.util;

/**
 * Represents a type of a catalog that can be used to identify types
 * without using an {@link Enum}.
 */
public interface CatalogType {

    /**
     * Gets the unique identifier of this {@link CatalogType}. The identifier
     * can be formatted however needed.
     *
     * @return The unique identifier of this catalog type
     */
    String getId();

    /**
     * Gets the unique human-readable name of this individual
     * {@link CatalogType}.
     *
     * @return The uniquely identifiable name of this catalog type
     */
    String getName();

}
