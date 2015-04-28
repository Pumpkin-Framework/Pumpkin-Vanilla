package nl.jk_5.pumpkin.server.permissions;

import org.junit.Assert;
import org.junit.Test;

public class PermissionsListTest {

    @Test
    public void testWildcardPermissionChecks() throws Exception {
        PermissionsList list = new PermissionsList();
        list.set("a.c.f.g", true);
        list.set("a.c.f.*", false);
        list.set("a.c.*", true);

        Assert.assertTrue(list.has("a.c.f.g"));
        Assert.assertFalse(list.has("a.c.f.h"));
        Assert.assertTrue(list.has("a.c.d"));
        Assert.assertFalse(list.has("a.c.d.g"));
    }

    @Test
    public void testRecursiveWildcardPermissionChecks() throws Exception {
        PermissionsList list = new PermissionsList();
        list.set("a.c.f.g", true);
        list.set("a.c.f.*", false);
        list.set("a.c.**", true);

        Assert.assertTrue(list.has("a.c.f.g"));
        Assert.assertFalse(list.has("a.c.f.h"));
        Assert.assertTrue(list.has("a.c.d"));
        Assert.assertTrue(list.has("a.c.d.g"));
        Assert.assertTrue(list.has("a.c.d.g.f"));
    }

    @Test
    public void testWildcardPermissionValues() throws Exception {
        PermissionsList list = new PermissionsList();
        list.set("a.c.f.g", "a");
        list.set("a.c.f.*", "b");
        list.set("a.c.*", "c");

        Assert.assertEquals("a", list.get("a.c.f.g"));
        Assert.assertEquals("b", list.get("a.c.f.h"));
        Assert.assertEquals("c", list.get("a.c.d"));
        Assert.assertNull(list.get("a.c.d.g"));
    }

    @Test
    public void testRecursiveWildcardPermissionValues() throws Exception {
        PermissionsList list = new PermissionsList();
        list.set("a.c.f.g", "a");
        list.set("a.c.f.*", "b");
        list.set("a.c.**", "c");

        Assert.assertEquals("a", list.get("a.c.f.g"));
        Assert.assertEquals("b", list.get("a.c.f.h"));
        Assert.assertEquals("c", list.get("a.c.d"));
        Assert.assertEquals("c", list.get("a.c.d.g"));
        Assert.assertEquals("c", list.get("a.c.d.g.f"));

        list.dump();

    }
}
