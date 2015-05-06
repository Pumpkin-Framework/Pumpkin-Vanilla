package nl.jk_5.pumpkin.server.event;

import nl.jk_5.pumpkin.api.event.Order;

class RegisteredHandler implements Comparable<RegisteredHandler> {

    private final Handler handler;
    private final Order order;

    RegisteredHandler(Handler handler, Order order) {
        this.handler = handler;
        this.order = order;
    }

    static RegisteredHandler createForComparison(Handler handler) {
        return new RegisteredHandler(handler, null);
    }

    public Handler getHandler() {
        return this.handler;
    }

    public Order getOrder() {
        return this.order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisteredHandler that = (RegisteredHandler) o;
        return this.handler.equals(that.handler);
    }

    @Override
    public int hashCode() {
        return this.handler.hashCode();
    }

    @Override
    public int compareTo(RegisteredHandler o) {
        return getOrder().ordinal() - o.getOrder().ordinal();
    }

}
