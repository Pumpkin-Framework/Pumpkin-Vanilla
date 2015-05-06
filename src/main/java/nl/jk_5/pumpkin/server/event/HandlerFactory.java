package nl.jk_5.pumpkin.server.event;

import java.lang.reflect.Method;

interface HandlerFactory {

    Handler createHandler(Object object, Method method, boolean ignoreCancelled);

}
