package nl.jk_5.pumpkin.server.permissions;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

@NonnullByDefault
public class GroupEntry implements Comparable<GroupEntry> {

    private final String group;
    private final int priority;
    private final int originalPriority;

    public GroupEntry(String group, int priority, int originalPriority) {
        this.group = group;
        this.priority = priority;
        this.originalPriority = originalPriority;
    }

    public GroupEntry(String group, int priority){
        this(group, priority, priority);
    }

    /*public GroupEntry(ServerZone zone, String group){
        this(group, parseIntDefault(zone.getGroupPermission(group, PumpkinPermissions.GROUP_PRIORITY), PumpkinPermissions.GROUP_PRIORITY_DEFAULT));
    }

    public GroupEntry(ServerZone zone, String group, int priority){
        this(group, priority, parseIntDefault(zone.getGroupPermission(group, PumpkinPermissions.GROUP_PRIORITY), PumpkinPermissions.GROUP_PRIORITY_DEFAULT));
    }*/

    public String getGroup() {
        return group;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return group;
    }

    @Override
    public int compareTo(GroupEntry o){
        int c = -Integer.compare(priority, o.priority);
        if(c != 0){
            return c;
        }
        c = -Integer.compare(originalPriority, o.originalPriority);
        if(c != 0){
            return c;
        }
        return group.compareTo(o.group);
    }

    public static List<String> toList(Collection<GroupEntry> entries){
        final List<String> result = new ArrayList<String>(entries.size());
        for(GroupEntry entry : entries){
            result.add(entry.group);
        }
        return result;
    }

    private static int parseIntDefault(@Nullable String value, int defaultValue){
        if(value == null){
            return defaultValue;
        }
        try{
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            return defaultValue;
        }
    }
}
