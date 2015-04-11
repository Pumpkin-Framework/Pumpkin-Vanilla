package nl.jk_5.pumpkin.api.mappack;

public enum Dimension {
    NETHER(-1),
    OVERWORLD(0),
    END(1);

    private final int id;

    Dimension(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Dimension parse(String in){
        if(in.equalsIgnoreCase("overworld")) return OVERWORLD;
        if(in.equalsIgnoreCase("nether")) return NETHER;
        if(in.equalsIgnoreCase("end")) return END;
        return OVERWORLD;
    }
}
