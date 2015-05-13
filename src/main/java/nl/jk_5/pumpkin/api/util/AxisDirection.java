package nl.jk_5.pumpkin.api.util;

/**
 * Represents the directional axis is either
 * positive, zero, or negative.
 */
public enum AxisDirection {

    PLUS(1),
    ZERO(0),
    MINUS(-1);

    private final int signum;

    private AxisDirection(final int signum) {
        this.signum = signum;
    }

    /**
     * Gets the signum for this direction.
     * <p>
     * A positive direction has a value of 1, negative direction -1 and 0 for no
     * direction.
     * </p>
     *
     * @return The signum
     */
    public int getSignum() {
        return this.signum;
    }

}
