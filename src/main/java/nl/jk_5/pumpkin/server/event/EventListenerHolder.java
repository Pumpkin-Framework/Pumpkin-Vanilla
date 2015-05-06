package nl.jk_5.pumpkin.server.event;

public abstract class EventListenerHolder<T> {

    protected PriorityEventListener<T>[] listeners;

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected EventListenerHolder() {
        this.listeners = new PriorityEventListener[0];
    }

    public boolean isEmpty() {
        return this.listeners.length == 0;
    }

    public void add(PriorityEventListener<T> listener) {
        if (listener.getHolder() != null) {
            throw new IllegalArgumentException("Listener already contained in a listener holder");
        }
        listener.setHolder(this);

        @SuppressWarnings({"unchecked", "rawtypes"})
        PriorityEventListener<T>[] newListeners = new PriorityEventListener[this.listeners.length + 1];
        int i;
        for (i = 0; i < this.listeners.length; i++) {
            if (listener.compareTo(this.listeners[i]) <= 0) {
                break;
            }
            newListeners[i] = this.listeners[i];
        }
        newListeners[i++] = listener;
        for (; i < newListeners.length; i++) {
            newListeners[i] = this.listeners[i - 1];
        }
        this.listeners = newListeners;
    }

    public void remove(PriorityEventListener<T> listener) {
        if (listener.getHolder() != this) {
            throw new IllegalArgumentException("EventListenerHolder does not contain event");
        }
        @SuppressWarnings({"unchecked", "rawtypes"})
        PriorityEventListener<T>[] newListeners = new PriorityEventListener[this.listeners.length - 1];
        int i = 0;
        int j = 0;
        while (i < this.listeners.length) {
            if (this.listeners[i] == listener) {
                i++;
                break;
            }
            newListeners[j++] = this.listeners[i++];
        }
        while (i < this.listeners.length) {
            newListeners[j++] = this.listeners[i++];
        }
        listener.setHolder(null);
        this.listeners = newListeners;
    }

    public void invoke(T event) {
        for (PriorityEventListener<T> listener : this.listeners) {
            listener.invoke(event);
        }
    }

}