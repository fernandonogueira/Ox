package ox.utils;

import ox.engine.internal.ResolvedMigration;

import java.util.*;
import java.util.Map.Entry;

public final class CollectionUtils {

    /**
     * Utility class
     */
    private CollectionUtils() {
    }

    public static boolean isMapOrderlyEquals(Map<?, ?> map1, Map<?, ?> map2) {

        if (map1 == null) {
            return map2 == null;
        } else {
            if (map2 == null) {
                return false;
            }
        }

        if (map1.size() != map2.size()) {
            return false;
        }

        Set<? extends Entry<?, ?>> entrySet1 = map1.entrySet();
        Set<? extends Entry<?, ?>> entrySet2 = map2.entrySet();

        Iterator<? extends Entry<?, ?>> iterator1 = entrySet1.iterator();
        Iterator<? extends Entry<?, ?>> iterator2 = entrySet2.iterator();

        while (iterator1.hasNext()) {

            if (!iterator2.hasNext()) {
                return false;
            }

            Entry<?, ?> object1 = iterator1.next();
            Entry<?, ?> object2 = iterator2.next();

            if (!object1.getKey().equals(object2.getKey()) || !object1.getValue().equals(object2.getValue())) {
                return false;
            }

        }

        return true;

    }

    public static List<ResolvedMigration> sortResolvedMigrations(List<ResolvedMigration> resolvedMigrations) {

        Comparator<ResolvedMigration> comparator = new Comparator<ResolvedMigration>() {
            @Override
            public int compare(ResolvedMigration o1, ResolvedMigration o2) {
                if (o1 == null) {
                    if (o2 == null) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    if (o2 == null) {
                        return 1;
                    }
                }

                if (o1.getVersion().equals(o2.getVersion())) {
                    return 0;
                }

                if (o1.getVersion().longValue() < o2.getVersion().longValue()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        };

        ArrayList<ResolvedMigration> newList = new ArrayList<>(resolvedMigrations);
        Collections.sort(newList, comparator);
        return newList;
    }
}
