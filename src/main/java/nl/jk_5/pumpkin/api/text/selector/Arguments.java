package nl.jk_5.pumpkin.api.text.selector;

/**
 * Utility class to create {@link Argument}s.
 */
public final class Arguments {

    private Arguments() {
    }

    /**
     * Creates a new {@link Argument} using the specified type and value.
     *
     * @param type The type of the argument
     * @param value The value of the argument
     * @param <T> The type of the argument value
     * @return The created argument
     */
    public static <T> Argument<T> create(ArgumentType<T> type, T value) {
        return Selectors.factory.createArgument(type, value);
    }

    /**
     * Creates a new {@link Argument.Invertible} using the specified type and
     * value. The created {@link Argument} will not be inverted.
     *
     * @param type The type of the invertible argument
     * @param value The value of the invertible argument
     * @param <T> The type of the argument value
     * @return The created invertible argument
     */
    public static <T> Argument.Invertible<T> create(ArgumentType.Invertible<T> type, T value) {
        return create(type, value, false);
    }

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
    public static <T> Argument.Invertible<T> create(ArgumentType.Invertible<T> type, T value, boolean inverted) {
        return Selectors.factory.createArgument(type, value, inverted);
    }

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
    public static Argument<?> parse(String argument) throws IllegalArgumentException {
        return Selectors.factory.parseArgument(argument);
    }

}
