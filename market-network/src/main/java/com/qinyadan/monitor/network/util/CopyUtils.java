package com.qinyadan.monitor.network.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


@SuppressWarnings("rawtypes")
public final class CopyUtils {

    private CopyUtils() {
    }

    public static Map<Object, Object> mediumCopyMap(Map<Object, Object> original) {
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();

        for (Map.Entry<Object, Object> entry : original.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            result.put(mediumCopy(key), mediumCopy(value));
        }
        return result;
    }

    public static Collection<Object> mediumCopyCollection(Collection<Object> original) {
        return new ArrayList<Object>(original);
    }

    @SuppressWarnings("unchecked")
	private static Object mediumCopy(Object original) {
        if (original instanceof Map) {
            return mediumCopyMap((Map) original);
        } else if (original instanceof Collection) {
            return mediumCopyCollection((Collection) original);
        } else {
            return original;
        }
    }

}
