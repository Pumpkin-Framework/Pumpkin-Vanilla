package nl.jk_5.pumpkin.api.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = RUNTIME)
@Target(value = METHOD)
public @interface EventHandler {

    /**
     * The order this handler should be called in relation to other handlers in
     * the {@link EventManager}.
     *
     * @return The order the handler should be called in
     */
    Order order() default Order.DEFAULT;

    /**
     * Whether this handler should execute even if the event has been cancelled
     * by another handler.
     *
     * @return If the handler should ignore cancelled events
     */
    boolean ignoreCancelled() default true;

}
