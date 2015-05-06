package nl.jk_5.pumpkin.server.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import nl.jk_5.pumpkin.api.event.Order;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

class HandlerCache {

    private final List<Handler> handlers;
    private final EnumMap<Order, List<Handler>> orderGrouped;

    @SuppressWarnings("unchecked")
    HandlerCache(List<RegisteredHandler> registrations) {
        this.handlers = Lists.newArrayList();
        for (RegisteredHandler reg : registrations) {
            this.handlers.add(reg.getHandler());
        }

        this.orderGrouped = Maps.newEnumMap(Order.class);
        for (Order order : Order.values()) {
            this.orderGrouped.put(order, new ArrayList<Handler>());
        }
        for (RegisteredHandler reg : registrations) {
            this.orderGrouped.get(reg.getOrder()).add(reg.getHandler());
        }
    }

    public List<Handler> getHandlers() {
        return this.handlers;
    }

    public List<Handler> getHandlersByOrder(Order order) {
        return this.orderGrouped.get(order);
    }

}
