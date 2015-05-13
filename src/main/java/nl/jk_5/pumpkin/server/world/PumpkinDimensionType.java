package nl.jk_5.pumpkin.server.world;

import com.google.common.base.Objects;

import nl.jk_5.pumpkin.api.world.DimensionType;

public class PumpkinDimensionType implements DimensionType {

    private String name;
    private int dimensionTypeId;

    public PumpkinDimensionType(String name, int id) {
        this.name = name;
        this.dimensionTypeId = id;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getDimensionTypeId() {
        return this.dimensionTypeId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", this.name)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DimensionType)) {
            return false;
        }

        DimensionType other = (DimensionType) obj;
        if (!this.name.equals(other.getName())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode(); // todo this is a warning
    }
}
