package nl.jk_5.pumpkin.server.util;

import nl.jk_5.pumpkin.api.gamemode.GameMode;
import nl.jk_5.pumpkin.api.text.translation.Translation;
import nl.jk_5.pumpkin.server.text.translation.PumpkinTranslation;

public class PumpkinGameMode implements GameMode {

    private String name;

    public PumpkinGameMode(String name) {
        this.name = name;
    }

    @Override
    public Translation getTranslation() {
        return new PumpkinTranslation(this.name.toUpperCase());
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
