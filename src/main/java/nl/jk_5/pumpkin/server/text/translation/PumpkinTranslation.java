package nl.jk_5.pumpkin.server.text.translation;

import net.minecraft.util.StatCollector;

import nl.jk_5.pumpkin.api.text.translation.Translation;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.Locale;

@NonnullByDefault
public class PumpkinTranslation implements Translation {

    private final String id;

    public PumpkinTranslation(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String get(Locale locale) {
        return StatCollector.translateToLocal(this.id);
    }

    @Override
    public String get(Locale locale, Object... args) {
        return StatCollector.translateToLocalFormatted(this.id, args);
    }

}
