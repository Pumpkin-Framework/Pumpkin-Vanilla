package nl.jk_5.pumpkin.server.sql;

import net.minecraft.world.GameRules;

import nl.jk_5.pumpkin.api.mappack.GameRule;
import nl.jk_5.pumpkin.api.mappack.Mappack;

public class SqlGamerules extends GameRules {

    public SqlGamerules(Mappack mappack) {
        super();

        for(GameRule rule : mappack.getGameRules()){
            this.setOrCreateGameRule(rule.getKey(), rule.getValue());
        }
    }
}
