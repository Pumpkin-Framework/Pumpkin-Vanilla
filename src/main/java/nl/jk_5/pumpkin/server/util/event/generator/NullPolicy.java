package nl.jk_5.pumpkin.server.util.event.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Determines how null parameters are handled.
 */
public enum NullPolicy {

    /**
     * Don't perform any null checking.
     */
    DISABLE_PRECONDITIONS,

    /**
     * Assume that all parameters are null unless they are annotated with
     * {@link Nullable}.
     */
    NON_NULL_BY_DEFAULT,

    /**
     * Assume that all parameters are nullable unless they are annotated with
     * {@link Nonnull}.
     */
    NULL_BY_DEFAULT

}
