package nl.jk_5.pumpkin.api.mappack;

import java.util.Collection;

public interface Mappack {

    int getId();

    String getName();

    Collection<MappackAuthor> getAuthors();

    Collection<MappackWorld> getWorlds();

    Collection<MappackFile> getFiles();
}
