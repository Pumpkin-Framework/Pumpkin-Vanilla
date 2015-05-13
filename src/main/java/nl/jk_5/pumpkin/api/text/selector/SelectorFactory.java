package nl.jk_5.pumpkin.api.text.selector;

/**
 * Represents the required implementation for the static methods in
 * {@link Selectors}, {@link Arguments} and {@link ArgumentTypes}.
 */
public interface SelectorFactory {

    /**
     * Creates a {@link SelectorBuilder} with the specified type and no
     * arguments.
     *
     * @param type The type of the selector
     * @return A new selector builder with the specified type
     */
    SelectorBuilder createBuilder(SelectorType type);

    /**
     * Parses a {@link Selector} from the given selector string.
     *
     * @param selector The raw selector string
     * @return A new selector containing the given selector data
     */
    Selector parseRawSelector(String selector);

    /**
     * Creates a minimum and maximum {@link ArgumentType} filtering depending on
     * the score of the specified objective.
     *
     * @param name The objective name to use
     * @return The created argument type
     */
    ArgumentType.Limit<ArgumentType<Integer>> createScoreArgumentType(String name);

    /**
     * Creates a custom {@link ArgumentType} with the specified key.
     *
     * @param key The key to use for the argument
     * @return The created argument type
     */
    ArgumentType<String> createArgumentType(String key);

    /**
     * Creates a custom {@link ArgumentType} with the specified key and value.
     *
     * @param key The key to use for the argument
     * @param type The class of the argument's value type
     * @param <T> The argument's value type
     * @return The created argument type
     */
    <T> ArgumentType<T> createArgumentType(String key, Class<T> type);

    /**
     * Creates a new {@link Argument} using the specified type and value.
     *
     * @param type The type of the argument
     * @param value The value of the argument
     * @param <T> The type of the argument value
     * @return The created argument
     */
    <T> Argument<T> createArgument(ArgumentType<T> type, T value);

    /**
     * Creates a new {@link Argument.Invertible} using the specified type and
     * value. The created {@link Argument} will be inverted based on the given
     * parameter.
     *
     * @param type The type of the invertible argument
     * @param value The value of the invertible argument
     * @param inverted {@code true} if the argument should be inverted
     * @param <T> The type of the argument value
     * @return The created invertible argument
     */
    <T> Argument.Invertible<T> createArgument(ArgumentType.Invertible<T> type, T value, boolean inverted);

    /**
     * Parses an {@link Argument} from the given argument string.
     *
     * <p>In Vanilla, it should be formatted like {@code key=value}.</p>
     *
     * @param argument The argument string
     * @return The parsed argument
     * @throws IllegalArgumentException If the argument couldn't be parsed (e.g.
     *         due to invalid format)
     */
    Argument<?> parseArgument(String argument) throws IllegalArgumentException;

}
