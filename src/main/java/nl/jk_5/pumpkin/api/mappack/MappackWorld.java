package nl.jk_5.pumpkin.api.mappack;

public interface MappackWorld {

    Mappack getMappack();

    String getName();

    String getGenerator();

    String getDimension();

    boolean isDefault();
}
