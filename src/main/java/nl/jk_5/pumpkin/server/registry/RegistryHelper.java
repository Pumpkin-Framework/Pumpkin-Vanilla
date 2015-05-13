package nl.jk_5.pumpkin.server.registry;

import com.google.common.base.Function;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class RegistryHelper {

    public static boolean mapFields(Class<?> apiClass, Map<String, ?> mapping, Collection<String> ignoredFields) {
        boolean mappingSuccess = true;
        for (Field f : apiClass.getDeclaredFields()) {
            if (ignoredFields.contains(f.getName())) {
                continue;
            }
            try {
                if (!mapping.containsKey(f.getName())) {
                    continue;
                }
                f.set(null, mapping.get(f.getName()));
            } catch (Exception e) {
                e.printStackTrace();
                mappingSuccess = false;
            }
        }
        return mappingSuccess;
    }

    public static boolean mapFields(Class<?> apiClass, Function<String, ?> mapFunction) {
        boolean mappingSuccess = true;
        for (Field f : apiClass.getDeclaredFields()) {
            try {
                f.set(null, mapFunction.apply(f.getName()));
            } catch (Exception e) {
                e.printStackTrace();
                mappingSuccess = false;
            }
        }
        return mappingSuccess;
    }

    public static boolean mapFields(Class<?> apiClass, Map<String, ?> mapping) {
        return mapFields(apiClass, mapping, Collections.<String>emptyList());
    }

    public static boolean setFactory(Class<?> apiClass, Object factory) {
        try {
            apiClass.getDeclaredField("factory").set(null, factory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
