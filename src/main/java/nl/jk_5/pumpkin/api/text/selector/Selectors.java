package nl.jk_5.pumpkin.api.text.selector;

/**
 * Utility class to work with and create Selectors.
 */
public final class Selectors {

    static final SelectorFactory factory = null;

    private Selectors() {
    }

    /**
     * Creates a {@link SelectorBuilder} with the specified type and no
     * arguments.
     *
     * @param type The type of the selector
     * @return A new selector builder with the specified type
     */
    public static SelectorBuilder builder(SelectorType type) {
        return factory.createBuilder(type);
    }

    /**
     * Parses a {@link Selector} from the given selector string.
     *
     * @param selector The raw selector string
     * @return A new selector containing the given selector data
     */
    public static Selector parse(String selector) {
        return factory.parseRawSelector(selector);
    }

}
