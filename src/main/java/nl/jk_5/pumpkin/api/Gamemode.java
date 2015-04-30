package nl.jk_5.pumpkin.api;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.HashMap;
import java.util.Map;

@NonnullByDefault
public enum Gamemode {
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    private static final Map<String, Gamemode> BY_NAME = new HashMap<String, Gamemode>();
    private static final TIntObjectMap<Gamemode> BY_ID = new TIntObjectHashMap<Gamemode>();

    private final int id;

    static {
        for(Gamemode value : values()){
            BY_NAME.put(value.name().toLowerCase(), value);
            BY_ID.put(value.getId(), value);
        }
    }

    Gamemode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Gamemode getById(int id){
        return BY_ID.get(id);
    }

    public static Gamemode getByName(String name){
        return BY_NAME.get(name);
    }
}
