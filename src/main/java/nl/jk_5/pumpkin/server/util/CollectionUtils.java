package nl.jk_5.pumpkin.server.util;

import java.util.List;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> toType(List in, Class<T> type){
        return (List<T>) in;
    }
}
