package nl.jk_5.pumpkin.server.util.location;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class Point {

    private static final Pattern pattern = Pattern.compile("\\s*\\[\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*\\]\\s*");

    private final int x;
    private final int y;
    private final int z;

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Point setX(int x){
        return new Point(x, y, z);
    }

    public Point setY(int y){
        return new Point(x, y, z);
    }

    public Point setZ(int z){
        return new Point(x, y, z);
    }

    public double length(){
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double distance(Point v){
        return Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) + (z - v.z) * (z - v.z));
    }

    public Point add(Point v){
        return new Point(x + v.x, y + v.y, z + v.z);
    }

    public Point subtract(Point v){
        return new Point(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Checks if two points are on the same plane (have the same coordinate on at least one axis)
     */
    public boolean alignsWith(Point point){
        return x == point.x || y == point.y || z == point.z;
    }

    /**
     * Checks if this point has greater or equal coordinates than another point on all axes
     */
    public boolean isGreaterEqualThan(Point p){
        return x >= p.x && y >= p.y && z >= p.z;
    }

    /**
     * Checks if this point has less or equal coordinates than another point on all axes
     */
    public boolean isLessEqualThan(Point p){
        return x <= p.x && y <= p.y && z <= p.z;
    }

    public Point positiveY(){
        return new Point(x, y < 0 ? 0 : y, z);
    }

    @Override
    public String toString(){
        return "[" + x + ", " + y + ", " + z + "]";
    }

    @Nullable
    public static Point fromString(String value){
        Matcher match = pattern.matcher(checkNotNull(value, "value"));
        if(!match.matches()){
            return null;
        }
        return new Point(Integer.parseInt(match.group(1)), Integer.parseInt(match.group(2)), Integer.parseInt(match.group(3)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        if (y != point.y) return false;
        return z == point.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }
}
