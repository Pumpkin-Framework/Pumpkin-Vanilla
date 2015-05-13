package nl.jk_5.pumpkin.api.util.annotation;

import java.lang.annotation.*;
import javax.annotation.Nonnull;

/**
 * Represents a class that is intended to represent a type of enum, without
 * using {@link Enum}. The class marked as {@link CatalogedBy} must have a
 * registrar class that can be queried for all types and subtypes of the
 * catalog.
 */
@Nonnull
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CatalogedBy {

    /**
     * Gets the class that is the catalog for this {@link CatalogedBy} type.
     * Since the type class annotated with {@link CatalogedBy} knows what
     * the catalog class is, we can safely rely on the value to get all
     * known instances of the {@link CatalogedBy}.
     *
     * <p>This is similar to knowing at runtime that all available
     * "EntityType"(s) are cataloged in the "EntityTypes" class.</p>
     *
     * @return The registrar class of the catalog
     */
    Class<?>[] value();

}
