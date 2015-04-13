package nl.jk_5.pumpkin.server.sql;

import net.minecraft.world.GameRules;

import nl.jk_5.pumpkin.api.mappack.GameRule;
import nl.jk_5.pumpkin.api.mappack.Mappack;

public class SqlGamerules extends GameRules {

    private final Mappack mappack;

    public SqlGamerules(Mappack mappack) {
        super();
        this.mappack = mappack;

        for(GameRule rule : this.mappack.getGameRules()){
            this.setOrCreateGameRule(rule.getKey(), rule.getValue());
        }
    }
}
