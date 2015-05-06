package nl.jk_5.pumpkin.server.event;

import nl.jk_5.pumpkin.api.event.EventListener;
import nl.jk_5.pumpkin.api.event.Order;

public class PriorityEventListener<T> implements EventListener<T>, Comparable<PriorityEventListener<T>> {

    private final EventListener<T> listener;
    private final Order order;
    private EventListenerHolder<T> holder;

    public PriorityEventListener(Order order, EventListener<T> listener) {
        this.listener = listener;
        this.order = order;
    }

    public EventListenerHolder<T> getHolder() {
        return this.holder;
    }

    public void setHolder(EventListenerHolder<T> holder) {
        this.holder = holder;
    }

    @Override
    public void invoke(T event) {
        this.listener.invoke(event);
    }

    @Override
    public int compareTo(PriorityEventListener<T> o) {
        return this.order.compareTo(o.order);
    }

}
