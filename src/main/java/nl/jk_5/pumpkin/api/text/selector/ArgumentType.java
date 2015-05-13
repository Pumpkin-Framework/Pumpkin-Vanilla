package nl.jk_5.pumpkin.api.text.selector;

import com.google.common.base.Optional;

/**
 * Represents the type of an {@link Argument}. This may represent a single
 * argument key in a {@link Selector}, or a compound of multiple argument keys.
 *
 * @param <T> The type for the value of this argument type
 * @see Selector
 * @see Argument
 * @see ArgumentTypes
 */
public interface ArgumentType<T> {

    /**
     * Returns the key associated with this {@link ArgumentType}. This will be
     * available if the argument type is not a compound of several ones.
     *
     * @return The key of this argument type, if available
     */
    Optional<String> getKey();

    /**
     * Represents an {@link ArgumentType} that can be inverted.
     *
     * @param <T> The type for the value of this argument type
     * @see ArgumentType
     * @see Argument.Invertible
     */
    interface Invertible<T> extends ArgumentType<T> {

    }

    /**
     * Represents a compound {@link ArgumentType} representing a 3-dimensional
     * vector.
     *
     * @param <V> The type of the vector
     * @param <T> The type of the components of the vector
     */
    interface Vector3<V, T> extends ArgumentType<V> {

        /**
         * Gets the {@link ArgumentType} for the x coordinate of this
         * {@link Vector3} {@link ArgumentType}.
         *
         * @return The x coordinate argument type
         */
        ArgumentType<T> x();

        /**
         * Gets the {@link ArgumentType} for the y coordinate of this
         * {@link Vector3} {@link ArgumentType}.
         *
         * @return The y coordinate argument type
         */
        ArgumentType<T> y();

        /**
         * Gets the {@link ArgumentType} for the z coordinate of this
         * {@link Vector3} {@link ArgumentType}.
         *
         * @return The z coordinate argument type
         */
        ArgumentType<T> z();

    }

    /**
     * Represents the holder of two {@link ArgumentType}s with a minimal and
     * maximal argument type.
     *
     * @param <T> The type of the argument type
     */
    interface Limit<T extends ArgumentType<?>> {

        /**
         * Returns the minimum {@link ArgumentType} of this {@link Limit}.
         *
         * @return The minimum argument type
         */
        T minimum();

        /**
         * Returns the maximum {@link ArgumentType} of this {@link Limit}.
         *
         * @return The maximum argument type
         */
        T maximum();

    }

}
