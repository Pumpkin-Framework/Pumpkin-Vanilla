package nl.jk_5.pumpkin.api.util;

import com.google.common.base.Optional;

import javax.annotation.Nullable;

/**
 * Utility for working with {@code Optional&lt;Boolean&gt;}s.
 *
 * <p>This also saves memory by holding three static instances of {@code Optional&lt;Boolean&gt;},
 * which represents the possible states it can have.</p>
 */
public final class OptBool {

    private OptBool() {}

    /**
     * The true value.
     */
    public static final Optional<Boolean> TRUE = Optional.of(true);

    /**
     * The false value.
     */
    public static final Optional<Boolean> FALSE = Optional.of(false);

    /**
     * The absent value.
     *
     * <p>Also a shorthand for constructing instances
     * with {@code Optional.&lt;Boolean&gt;absent()}.</p>
     */
    public static final Optional<Boolean> ABSENT = Optional.absent();

    /**
     * Constructs a new {@code Optional&lt;Boolean&gt;} from the given boolean.
     *
     * @param bool The boolean
     * @return The constructed Optional
     */
    public static Optional<Boolean> of(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    /**
     * Constructs a new {@code Optional&lt;Boolean&gt;} from the given {@link Boolean}.
     *
     * @param bool The boolean
     * @return The constructed Optional, or {@link Optional#absent()}
     */
    public static Optional<Boolean> of(@Nullable Boolean bool) {
        if (bool != null) {
            return of(bool.booleanValue());
        } else {
            return ABSENT;
        }
    }

    /**
     * Coerces the given {@code Optional&lt;Boolean&gt;} into one of the three stored states.
     *
     * @param bool The boolean
     * @return The constructed Optional, or {@link Optional#absent()}
     */
    public static Optional<Boolean> of(Optional<Boolean> bool) {
        if (bool.isPresent()) {
            return of(bool.get().booleanValue());
        } else {
            return ABSENT;
        }
    }

}
