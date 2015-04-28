package nl.jk_5.pumpkin.server.permissions.zone;

import static com.google.common.base.Preconditions.checkNotNull;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;
import nl.jk_5.pumpkin.server.util.location.Point;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@NonnullByDefault
public class Area {

    private static final Pattern pattern = Pattern.compile("\\s*\\{\\s*(\\[.*\\])\\s*,\\s*(\\[.*\\])\\s*\\}\\s*");

    protected final Point high;
    protected final Point low;

    public Area(Point p1, Point p2) {
        checkNotNull(p1, "p1");
        checkNotNull(p2, "p2");

        this.high = getMaxPoint(p1, p2);
        this.low = getMinPoint(p1, p2);
    }

    public int getXLength(){
        return high.getX() - low.getX() + 1;
    }

    public int getYLength(){
        return high.getY() - low.getY() + 1;
    }

    public int getZLength(){
        return high.getZ() - low.getZ() + 1;
    }

    public Point getHighPoint(){
        return high;
    }

    public Point getLowPoint(){
        return low;
    }

    public Point getSize(){
        return high.subtract(low);
    }

    public boolean contains(Point p){
        return high.isGreaterEqualThan(p) && low.isLessEqualThan(p);
    }

    public boolean contains(Area area){
        return this.contains(area.high) && this.contains(area.low);
    }

    public boolean intersectsWith(Area area){
        return this.getIntersection(area) != null;
    }

    /**
     * @param area
     *            The area to be checked.
     * @return NULL if the areas to do not intersect. Argument if this area completely contains the argument.
     */
    @Nullable
    public Area getIntersection(Area area){
        boolean hasIntersection = false;
        Point p = new Point(0, 0, 0);
        Point minp = new Point(0, 0, 0);
        Point maxp = new Point(0, 0, 0);
        int[] xs = {this.low.getX(), this.high.getX(), area.low.getX(), area.high.getX()};
        int[] ys = {this.low.getY(), this.high.getY(), area.low.getY(), area.high.getY()};
        int[] zs = {this.low.getZ(), this.high.getZ(), area.low.getZ(), area.high.getZ()};

        for(int x : xs){
            p.setX(x);
            for(int y : ys){
                p.setY(y);
                for(int z : zs){
                    p.setZ(z);

                    if(this.contains(p) && area.contains(p)){
                        if(!hasIntersection){
                            hasIntersection = true;
                            minp = p;
                            maxp = p;
                        }else{
                            minp = getMinPoint(minp, p);
                            maxp = getMaxPoint(maxp, p);
                        }
                    }
                }
            }
        }

        if(!hasIntersection){
            return null;
        }else{
            return new Area(minp, maxp);
        }
    }

    public boolean makesCuboidWith(Area area){
        boolean alignX = low.getX() == area.low.getX() && high.getX() == area.high.getX();
        boolean alignY = low.getY() == area.low.getY() && high.getY() == area.high.getY();
        boolean alignZ = low.getZ() == area.low.getZ() && high.getZ() == area.high.getZ();

        return alignX || alignY || alignZ;
    }

    @Nullable
    public Area getUnity(Area area){
        if(!makesCuboidWith(area)){
            return null;
        }else{
            return new Area(getMinPoint(low, area.low), getMaxPoint(high, area.high));
        }
    }

    public Area copy(){
        return new Area(low, high);
    }

    @Override
    public String toString(){
        return "{" + high.toString() + " , " + low.toString() + " }";
    }

    @Nullable
    public static Area fromString(String value){
        Matcher match = pattern.matcher(checkNotNull(value, "value"));
        if(!match.matches()){
            return null;
        }
        Point p1 = Point.fromString(match.group(1));
        Point p2 = Point.fromString(match.group(2));
        if(p1 == null || p2 == null){
            return null;
        }
        return new Area(p1, p2);
    }

    public Point getCenter(){
        return new Point((high.getX() + low.getX()) / 2, (high.getY() + low.getY()) / 2, (high.getZ() + low.getZ()) / 2);
    }

    private static Point getMinPoint(Point p1, Point p2){
        return new Point(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()), Math.min(p1.getZ(), p2.getZ()));
    }

    private static Point getMaxPoint(Point p1, Point p2){
        return new Point(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()), Math.max(p1.getZ(), p2.getZ()));
    }
}
