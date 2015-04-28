package nl.jk_5.pumpkin.server.permissions;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class UnmodifiablePermissionsList {

    private final PermissionsList list;

    public UnmodifiablePermissionsList(PermissionsList list) {
        this.list = list;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean has(String key) {
        return list.has(key);
    }

    @Nullable
    public String get(String key) {
        return list.get(key);
    }

    public List<String> toList() {
        return Collections.unmodifiableList(list.toList());
    }

    public void dump() {
        list.dump();
    }
}
