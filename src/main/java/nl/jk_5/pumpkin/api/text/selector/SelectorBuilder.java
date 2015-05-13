package nl.jk_5.pumpkin.api.text.selector;

/**
 * Represents a builder interface to create immutable {@link Selector}
 * instances.
 */
public interface SelectorBuilder {

    /**
     * Sets the type of this selector.
     *
     * @param type The type to set
     * @return This selector builder
     */
    SelectorBuilder type(SelectorType type);

    /**
     * Adds some arguments to this selector.
     *
     * @param arguments The arguments to add
     * @return This selector builder
     */
    SelectorBuilder add(Argument<?>... arguments);

    /**
     * Adds some arguments to this selector.
     *
     * @param arguments The arguments to add
     * @return This selector builder
     */
    SelectorBuilder add(Iterable<Argument<?>> arguments);

    /**
     * Adds a new {@link Argument} with the specified {@link ArgumentType} and
     * value to this selector.
     *
     * @param type The type of the argument
     * @param value The value of the argument
     * @param <T> The type of the argument value
     * @return This selector builder
     */
    <T> SelectorBuilder add(ArgumentType<T> type, T value);

    /**
     * Removes the specified arguments, if they exist.
     *
     * @param arguments The arguments to remove
     * @return This selector builder
     */
    SelectorBuilder remove(Argument<?>... arguments);

    /**
     * Removes the specified arguments, if they exist.
     *
     * @param arguments The arguments to remove
     * @return This selector builder
     */
    SelectorBuilder remove(Iterable<Argument<?>> arguments);

    /**
     * Removes the arguments with the specified {@link ArgumentType}, if they
     * exist.
     *
     * @param types The argument types
     * @return This selector builder
     */
    SelectorBuilder remove(ArgumentType<?>... types);

    /**
     * Builds an immutable instance of the current state of this selector
     * builder.
     *
     * @return An immutable {@link Selector} with the current properties of this
     *         builder
     */
    Selector build();

}
