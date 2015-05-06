package nl.jk_5.pumpkin.server.event;

import nl.jk_5.pumpkin.api.event.Event;

import java.lang.reflect.InvocationTargetException;

public interface Handler {

    void handle(Event event) throws InvocationTargetException;

}