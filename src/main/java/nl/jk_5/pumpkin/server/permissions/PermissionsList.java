package nl.jk_5.pumpkin.server.permissions;

import org.apache.commons.lang3.StringUtils;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.io.PrintStream;
import java.util.*;
import javax.annotation.Nullable;

@NonnullByDefault
public class PermissionsList {

    private static final MostDotsComparator MOST_DOTS_COMPARATOR = new MostDotsComparator();

    public static final String PERMISSION_ASTERIX = "*";
    public static final String PERMISSION_ASTERIX_RECURSIVE = "**";
    public static final String PERMISSION_FALSE = "false";
    public static final String PERMISSION_TRUE = "true";
    public static final String ALL_PERMS = "." + PERMISSION_ASTERIX;
    public static final String ALL_PERMS_RECURSIVE = "." + PERMISSION_ASTERIX_RECURSIVE;

    private final Map<String, String> map = new HashMap<String, String>();
    private final List<String> wildcards = new ArrayList<String>();
    private final List<String> recursiveWildcards = new ArrayList<String>();

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public String set(String key, int value){
        return set(key, Integer.toString(value));
    }

    public String set(String key, String value) {
        if(key.endsWith(PERMISSION_ASTERIX_RECURSIVE)){
            recursiveWildcards.add(key.substring(0, key.length() - 3));
            Collections.sort(recursiveWildcards, MOST_DOTS_COMPARATOR);
        }else if(key.endsWith(PERMISSION_ASTERIX)){
            wildcards.add(key.substring(0, key.length() - 2));
            Collections.sort(wildcards, MOST_DOTS_COMPARATOR);
        }
        return map.put(key, value);
    }

    public String set(String key, boolean value){
        if(key.endsWith(PERMISSION_ASTERIX_RECURSIVE)){
            recursiveWildcards.add(key.substring(0, key.length() - 3));
            Collections.sort(recursiveWildcards, MOST_DOTS_COMPARATOR);
        }else if(key.endsWith(PERMISSION_ASTERIX)){
            wildcards.add(key.substring(0, key.length() - 2));
            Collections.sort(wildcards, MOST_DOTS_COMPARATOR);
        }
        return map.put(key, value ? PERMISSION_TRUE : PERMISSION_FALSE);
    }

    public String remove(String key) {
        if(key.endsWith(PERMISSION_ASTERIX_RECURSIVE)){
            recursiveWildcards.remove(key.substring(0, key.length() - 3));
            Collections.sort(recursiveWildcards, MOST_DOTS_COMPARATOR);
        }else if(key.endsWith(PERMISSION_ASTERIX)){
            wildcards.remove(key.substring(0, key.length() - 2));
            Collections.sort(wildcards, MOST_DOTS_COMPARATOR);
        }
        return map.remove(key);
    }

    public void clear() {
        this.recursiveWildcards.clear();
        this.wildcards.clear();
        this.map.clear();
    }

    public boolean has(String key) {
        String value = get(key);
        return value != null && value.equalsIgnoreCase(PERMISSION_TRUE);
    }

    @Nullable
    public String get(String key){
        if(this.map.containsKey(key)){
            return this.map.get(key);
        }
        if(this.wildcards.isEmpty()){
            return null;
        }

        for(String wildcard : this.wildcards){
            if(key.startsWith(wildcard)){
                if(StringUtils.countMatches(wildcard, ".") + 1 == StringUtils.countMatches(key, ".")){
                    return this.map.get(wildcard + ALL_PERMS);
                }
            }
        }

        for(String wildcard : this.recursiveWildcards){
            if(key.startsWith(wildcard)){
                return this.map.get(wildcard + ALL_PERMS_RECURSIVE);
            }
        }

        return null;
    }

    public List<String> toList(){
        List<String> list = new ArrayList<String>();
        for(Map.Entry<String, String> perm : map.entrySet()){
            if(perm.getValue() == null){
                continue;
            }
            if(perm.getValue().equals(PERMISSION_TRUE)){
                list.add(perm.getKey());
            }else if (perm.getValue().equals(PERMISSION_FALSE)){
                list.add("-" + perm.getKey());
            }else{
                list.add(perm.getKey() + "=" + perm.getValue());
            }
        }
        Collections.sort(list);
        return list;
    }

    public static PermissionsList fromList(List<String> fromList){
        PermissionsList list = new PermissionsList();
        for(String permission : fromList){
            String[] permParts = permission.split("=");
            if(permParts.length == 2){
                list.set(permParts[0], permParts[1]);
            }else if(permParts.length == 1){
                if(permission.startsWith("-")){
                    list.set(permission.substring(1, permission.length()), PERMISSION_FALSE);
                }else{
                    list.set(permission, PERMISSION_TRUE);
                }
            }
        }
        return list;
    }

    public void dump(){
        PrintStream out = System.out;

        out.println("========== PermissionsList dump ==========");
        if(this.wildcards.isEmpty()){
            out.println("Wildcards: none");
        }else{
            out.println("Wildcards:");
            for(String wildcard : this.wildcards){
                out.println(" - " + wildcard);
            }
        }
        out.println();
        if(this.recursiveWildcards.isEmpty()){
            out.println("Recursive wildcards: none");
        }else{
            out.println("Recursive wildcards:");
            for(String wildcard : this.recursiveWildcards){
                out.println(" - " + wildcard);
            }
        }
        out.println();
        if(this.map.isEmpty()){
            out.println("Permissions: none");
        }else{
            out.println("Permissions:");
            for(Map.Entry<String, String> entry : this.map.entrySet()){
                out.println(" - " + entry.getKey() + " = " + entry.getValue());
            }
        }
        out.println("==========================================");
    }

    private static class MostDotsComparator implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            int a1 = StringUtils.countMatches(s1, ".");
            int a2 = StringUtils.countMatches(s2, ".");
            return (a2 < a1) ? -1 : ((a2 == a1) ? 0 : 1);
        }
    }
}
