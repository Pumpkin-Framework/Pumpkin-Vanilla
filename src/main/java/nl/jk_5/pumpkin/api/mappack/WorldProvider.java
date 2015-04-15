package nl.jk_5.pumpkin.api.mappack;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public interface WorldProvider {

    int getId();
    void setId(int id);

    /**
     * What kind of dimension is this world?
     * Possible options:
     * <ul>
     *     <li>NETHER</li>
     *     <li>OVERWORLD</li>
     *     <li>END</li>
     * </ul>
     *
     * Defaults to OVERWORLD
     *
     * @return the world dimension
     */
    Dimension getDimension();

    //TODO: Maybe an enum?
    String getType();

    @Nullable
    String getOptions();
}
