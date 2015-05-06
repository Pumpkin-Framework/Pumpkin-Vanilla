package nl.jk_5.pumpkin.server.util.event.reflect;

import java.util.*;

/**
 * A queue implementation that only permits an object to be added to the
 * queue once, while also rejecting null offers.
 *
 * @param <E> The contained type
 */
class NonNullUniqueQueue<E> extends AbstractQueue<E> implements Queue<E> {

    private final Queue<E> queue = new ArrayDeque<E>();
    private final Set<E> set = new HashSet<E>();

    @Override
    public Iterator<E> iterator() {
        return this.queue.iterator();
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public boolean offer(E o) {
        return o != null && this.set.add(o) && this.queue.offer(o);
    }

    @Override
    public E poll() {
        return this.queue.poll();
    }

    @Override
    public E peek() {
        return this.queue.peek();
    }
}
