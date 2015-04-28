package nl.jk_5.pumpkin.server.permissions.zone;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;
import nl.jk_5.pumpkin.server.util.location.Point;

import javax.annotation.Nullable;

@NonnullByDefault
public enum Shape {
    BOX,
    CYLINDER,
    ELLIPSOID;

    public boolean contains(Area area, Point point){
        if(!area.contains(point)){
            return false;
        }
        if(this == BOX){
            return true;
        }

        float dx = (float) (point.getX() - area.low.getX()) / (area.high.getX() - area.low.getX()) * 2 - 1;
        float dy = (float) (point.getY() - area.low.getY()) / (area.high.getY() - area.low.getY()) * 2 - 1;
        float dz = (float) (point.getZ() - area.low.getZ()) / (area.high.getZ() - area.low.getZ()) * 2 - 1;

        switch(this){
            case ELLIPSOID:
                return dx * dx + dy * dy + dz * dz <= 1;
            case CYLINDER:
                return dx * dx + dz * dz <= 1;
            case BOX:
            default:
                return true;
        }
    }

    public boolean contains(Area area, Area otherArea){
        if(this == BOX){
            return area.contains(otherArea);
        }
        Point p1 = new Point(otherArea.low.getX(), otherArea.low.getY(), otherArea.low.getZ());
        Point p2 = new Point(otherArea.low.getX(), otherArea.low.getY(), otherArea.high.getZ());
        Point p3 = new Point(otherArea.low.getX(), otherArea.high.getY(), otherArea.low.getZ());
        Point p4 = new Point(otherArea.low.getX(), otherArea.high.getY(), otherArea.high.getZ());
        Point p5 = new Point(otherArea.high.getX(), otherArea.low.getY(), otherArea.low.getZ());
        Point p6 = new Point(otherArea.high.getX(), otherArea.low.getY(), otherArea.high.getZ());
        Point p7 = new Point(otherArea.high.getX(), otherArea.high.getY(), otherArea.low.getZ());
        Point p8 = new Point(otherArea.high.getX(), otherArea.high.getY(), otherArea.high.getZ());
        return (contains(area, p1) && contains(area, p2) && contains(area, p3) && contains(area, p4) && contains(area, p5) && contains(area, p6) && contains(area, p7) && contains(area, p8));
    }

    @Nullable
    public static Shape getByName(String name){
        if(name.equalsIgnoreCase("box")){
            return BOX;
        }else if(name.equalsIgnoreCase("cylinder")){
            return CYLINDER;
        }else if(name.equalsIgnoreCase("ellipsoid")){
            return ELLIPSOID;
        }else{
            return null;
        }
    }

    public static String[] valueNames(){
        Shape[] values = values();
        String[] names = new String[values.length];
        for(int i = 0; i < values.length; i++){
            names[i] = values[i].toString();
        }
        return names;
    }
}
