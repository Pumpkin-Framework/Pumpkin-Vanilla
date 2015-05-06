package nl.jk_5.pumpkin.server.event;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.api.event.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public class PumpkinEventBus implements EventManager {

    private final Logger logger = LogManager.getLogger();
    private final Object lock = new Object();
    private final HandlerFactory handlerFactory = new HandlerClassFactory("nl.jk_5.pumpkin.server.event.handler");
    private final Multimap<Class<?>, RegisteredHandler> handlersByEvent = HashMultimap.create();

    /**
     * A cache of all the handlers for an event type for quick event posting.
     *
     * <p>
     * The cache is currently entirely invalidated if handlers are added or
     * removed.
     * </p>
     */
    private final LoadingCache<Class<?>, HandlerCache> handlersCache =
            CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, HandlerCache>() {

                @Override
                public HandlerCache load(Class<?> type) throws Exception {
                    return bakeHandlers(type);
                }
            });

    private static boolean isValidHandler(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        return !Modifier.isStatic(method.getModifiers())
                && !Modifier.isAbstract(method.getModifiers())
                && !Modifier.isInterface(method.getDeclaringClass().getModifiers())
                && method.getReturnType() == void.class
                && paramTypes.length == 1
                && Event.class.isAssignableFrom(paramTypes[0]);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private HandlerCache bakeHandlers(Class<?> rootType) {
        List<RegisteredHandler> registrations = Lists.newArrayList();
        Set<Class<?>> types = (Set) TypeToken.of(rootType).getTypes().rawTypes();

        synchronized (this.lock) {
            for (Class<?> type : types) {
                if (Event.class.isAssignableFrom(type)) {
                    registrations.addAll(this.handlersByEvent.get(type));
                }
            }
        }

        Collections.sort(registrations);

        return new HandlerCache(registrations);
    }

    private HandlerCache getHandlerCache(Class<?> type) {
        return this.handlersCache.getUnchecked(type);
    }

    @SuppressWarnings("unchecked")
    private List<Subscriber> findAllSubscribers(Object object) {
        List<Subscriber> subscribers = Lists.newArrayList();
        Class<?> type = object.getClass();

        for (Method method : type.getMethods()) {
            @Nullable
            EventHandler subscribe = method.getAnnotation(EventHandler.class);

            if (subscribe != null) {
                Class<?>[] paramTypes = method.getParameterTypes();

                if (isValidHandler(method)) {
                    Class<Event> eventClass = (Class<Event>) paramTypes[0];
                    Handler handler = this.handlerFactory.createHandler(object, method, subscribe.ignoreCancelled());
                    subscribers.add(new Subscriber(eventClass, handler, subscribe.order()));
                } else {
                    logger.warn("The method {} on {} has @{} but has the wrong signature", method, method.getDeclaringClass().getName(), EventHandler.class.getName());
                }
            }
        }

        return subscribers;
    }

    public boolean register(Class<?> type, Handler handler, Order order) {
        return register(new Subscriber(type, handler, order));
    }

    public boolean register(Subscriber subscriber) {
        return registerAll(Lists.newArrayList(subscriber));
    }

    @Override
    public void register(Object object) {
        checkNotNull(object, "object");

        registerAll(findAllSubscribers(object));
    }

    private boolean registerAll(List<Subscriber> subscribers) {
        synchronized (this.lock) {
            boolean changed = false;

            for (Subscriber sub : subscribers) {
                if (this.handlersByEvent.put(sub.getEventClass(), new RegisteredHandler(sub.getHandler(), sub.getOrder()))) {
                    changed = true;
                }
            }

            if (changed) {
                this.handlersCache.invalidateAll();
            }

            return changed;
        }
    }

    public boolean unregister(Class<?> type, Handler handler) {
        return unregister(new Subscriber(type, handler));
    }

    public boolean unregister(Subscriber subscriber) {
        return unregisterAll(Lists.newArrayList(subscriber));
    }

    @Override
    public void unregister(Object object) {
        checkNotNull(object, "object");
        unregisterAll(findAllSubscribers(object));
    }

    public boolean unregisterAll(List<Subscriber> subscribers) {
        synchronized (this.lock) {
            boolean changed = false;

            for (Subscriber sub : subscribers) {
                if (this.handlersByEvent.remove(sub.getEventClass(), RegisteredHandler.createForComparison(sub.getHandler()))) {
                    changed = true;
                }
            }

            if (changed) {
                this.handlersCache.invalidateAll();
            }

            return changed;
        }
    }

    private void callListener(Handler handler, Event event) {
        try {
            handler.handle(event);
        } catch (Throwable t) {
            logger.warn("A handler raised an error when handling an event", t);
        }
    }

    @Override
    public boolean post(Event event) {
        checkNotNull(event, "event");

        for (Handler handler : getHandlerCache(event.getClass()).getHandlers()) {
            callListener(handler, event);
        }

        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }

    public boolean post(Event event, Order order) {
        checkNotNull(event, "event");
        checkNotNull(event, "order");

        for (Handler handler : getHandlerCache(event.getClass()).getHandlersByOrder(order)) {
            callListener(handler, event);
        }

        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }

}
