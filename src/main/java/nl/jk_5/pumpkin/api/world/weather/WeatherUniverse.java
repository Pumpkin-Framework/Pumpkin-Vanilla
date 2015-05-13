package nl.jk_5.pumpkin.api.world.weather;

/**
 * A universe affected by {@link Weather}.
 */
public interface WeatherUniverse {

    /**
     * Gets the current {@link Weather} in this volume.
     *
     * @return The current weather.
     */
    Weather getWeather();

    /**
     * Gets the remaining duration of the current {@link Weather}.
     *
     * @return The remaining weather duration.
     */
    long getRemainingDuration();

    /**
     * Gets the duration the current {@link Weather} has been running for.
     *
     * @return The running weather duration.
     */
    long getRunningDuration();

    /**
     * Sets the {@link Weather} of the volume with a random duration.
     *
     * @param weather The new {@link Weather}.
     */
    void forecast(Weather weather);

    /**
     * Sets the {@link Weather} of the volume with the specified duration.
     *
     * @param weather The new {@link Weather}.
     * @param duration The specified duration.
     */
    void forecast(Weather weather, long duration);
}
