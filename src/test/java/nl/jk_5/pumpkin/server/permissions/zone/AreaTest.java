package nl.jk_5.pumpkin.server.permissions.zone;

import org.junit.Assert;
import org.junit.Test;

import nl.jk_5.pumpkin.server.util.location.Point;

public class AreaTest {

    @Test
    public void testHighLowPoint() throws Exception {
        Point p1 = new Point(10, 40, 10);
        Point p2 = new Point(20, 60, 20);

        Area area = new Area(p1, p2);

        Assert.assertEquals(p1, area.getLowPoint());
        Assert.assertEquals(p2, area.getHighPoint());

        area = new Area(p2, p1);

        Assert.assertEquals(p1, area.getLowPoint());
        Assert.assertEquals(p2, area.getHighPoint());
    }

    @Test
    public void testToString() throws Exception {
        Point p1 = new Point(10, 40, 10);
        Point p2 = new Point(20, 60, 20);

        Area area = new Area(p1, p2);

        Assert.assertEquals("{[20, 60, 20] , [10, 40, 10] }", area.toString());
    }

    @Test
    public void testFromString() throws Exception {
        Area area = Area.fromString("{[20, 60, 20] , [10, 40, 10] }");

        Assert.assertNotNull(area);
        Assert.assertNotNull(area.getLowPoint());
        Assert.assertNotNull(area.getHighPoint());
        Assert.assertEquals(new Point(10, 40, 10), area.getLowPoint());
        Assert.assertEquals(new Point(20, 60, 20), area.getHighPoint());
    }

    @Test
    public void testGetCenter() throws Exception {
        Point p1 = new Point(10, 40, 10);
        Point p2 = new Point(20, 60, 20);

        Area area = new Area(p1, p2);

        Assert.assertEquals(new Point(15, 50, 15), area.getCenter());
    }
}
