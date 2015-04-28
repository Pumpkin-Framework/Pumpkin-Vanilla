package nl.jk_5.pumpkin.server.player;

import com.mojang.authlib.GameProfile;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

@NonnullByDefault
class ProfilesList extends AbstractCollection<GameProfile> {

    private final PlayerManager manager;

    ProfilesList(PlayerManager manager) {
        this.manager = manager;
    }

    @Override
    public int size() {
        return manager.onlinePlayers.size();
    }

    @Override
    public boolean isEmpty() {
        return manager.onlinePlayers.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if(!(o instanceof GameProfile)){
            return false;
        }
        return manager.playersById.containsKey(((GameProfile) o).getId());
    }

    @Override
    public Iterator<GameProfile> iterator() {
        final Iterator<Player> it = manager.onlinePlayers.iterator();
        return new Iterator<GameProfile>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public GameProfile next() {
                return it.next().getGameProfile();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }

    @Override
    public boolean add(GameProfile profile) {
        throw new UnsupportedOperationException("add");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public boolean addAll(Collection<? extends GameProfile> c) {
        throw new UnsupportedOperationException("addAll");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear");
    }
}
