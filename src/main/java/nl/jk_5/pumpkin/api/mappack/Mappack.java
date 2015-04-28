package nl.jk_5.pumpkin.api.mappack;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.Collection;

@NonnullByDefault
public interface Mappack {

    int getId();

    String getName();

    Collection<MappackAuthor> getAuthors();

    Collection<MappackWorld> getWorlds();

    Collection<MappackFile> getFiles();

    Collection<GameRule> getGameRules();
}
