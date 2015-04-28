package nl.jk_5.pumpkin.server;

import nl.jk_5.pumpkin.server.permissions.PermissionsList;
import nl.jk_5.pumpkin.server.permissions.zone.Area;
import nl.jk_5.pumpkin.server.permissions.zone.Shape;
import nl.jk_5.pumpkin.server.util.location.Point;

public class ShapeTest {

    public static void main(String[] args) {
        Point p1 = new Point(10, 40, 10);
        Point p2 = new Point(20, 60, 20);

        Area area = new Area(p1, p2);

        Shape shape = Shape.BOX;

        PermissionsList list = new PermissionsList();
        list.set("a.c.f.g", false);
        list.set("a.c.f.*", false);
        list.set("a.c.*", true);

        System.out.println(list.has("a.c.f.g"));
        System.out.println(list.has("a.c.f.h"));
        System.out.println(list.has("a.c.d"));
        System.out.println(list.has("a.c.d.g"));

        //System.out.println(area.toString());
        //System.out.println(Area.fromString(area.toString()).toString());
    }
}
