package nl.jk_5.pumpkin.api.event;

public interface EventListener<T> {

    void invoke(T event);
}
