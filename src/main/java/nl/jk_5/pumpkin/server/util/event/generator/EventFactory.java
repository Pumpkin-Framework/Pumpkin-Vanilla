package nl.jk_5.pumpkin.server.util.event.generator;

import com.google.common.base.Function;

import java.util.Map;

/**
 * Generates a new instance of an event using a given map of parameters.
 *
 * @param <E> The type of event
 */
public interface EventFactory<E> extends Function<Map<String, Object>, E> {

}
