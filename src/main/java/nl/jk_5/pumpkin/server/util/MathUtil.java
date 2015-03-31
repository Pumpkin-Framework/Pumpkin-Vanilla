package nl.jk_5.pumpkin.server.util;

public final class MathUtil {

    private MathUtil(){
    }

    public static double square(double in){
        return in * in;
    }

    public static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }
}
