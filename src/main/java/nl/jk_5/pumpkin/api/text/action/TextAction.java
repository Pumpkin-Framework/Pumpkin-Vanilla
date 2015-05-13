package nl.jk_5.pumpkin.api.text.action;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import nl.jk_5.pumpkin.api.text.Text;

import javax.annotation.Nullable;

/**
 * Represents an action happening as a response to an event on a {@link Text}.
 *
 * @param <R> The type of the result
 *
 * @see ClickAction
 * @see HoverAction
 * @see ShiftClickAction
 */
public abstract class TextAction<R> {

    protected final R result;

    /**
     * Constructs a new {@link TextAction} with the given result.
     *
     * @param result The result of the text action
     */
    protected TextAction(R result) {
        this.result = checkNotNull(result, "result");
    }

    /**
     * Returns the result of this {@link TextAction}.
     *
     * @return The result
     */
    public final R getResult() {
        return this.result;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TextAction<?> that = (TextAction<?>) o;
        return this.result.equals(that.result);
    }

    @Override
    public int hashCode() {
        return this.result.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.result)
                .toString();
    }

}
