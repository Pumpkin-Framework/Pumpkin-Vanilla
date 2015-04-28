package nl.jk_5.pumpkin.server.util;

import java.util.List;

public class CollectionUtils {

    @SuppressWarnings("unchecked")
    public static <T> List<T> toType(List in, Class<T> type){
        return (List<T>) in;
    }
}
