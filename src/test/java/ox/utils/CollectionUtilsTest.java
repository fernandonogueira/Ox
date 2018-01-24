package ox.utils;

import org.junit.Assert;
import org.junit.Test;
import ox.engine.internal.resources.ResolvedMigration;
import ox.engine.structure.OrderingType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Fernando Nogueira
 * @since 4/17/14 11:38 AM
 */
public class CollectionUtilsTest {

    /**
     * Two maps with same attributes inserted in different order should be equals
     */
    @Test
    public void validateIfDifferentOrderedMapsAreEquals() {


        LinkedHashMap<String, OrderingType> map1 = new LinkedHashMap<String, OrderingType>();
        map1.put("attr1", OrderingType.ASC);
        map1.put("attr2", OrderingType.ASC);

        LinkedHashMap<String, OrderingType> map2 = new LinkedHashMap<String, OrderingType>();
        map2.put("attr2", OrderingType.ASC);
        map2.put("attr1", OrderingType.ASC);

        Assert.assertEquals(
                "Different sorted maps should not be equals",
                false,
                CollectionUtils.isMapOrderlyEquals(map1, map2));

    }

    /**
     * Two maps with same attributes inserted in the same order should be equals
     */
    @Test
    public void validateIfEquallyOrderedMapsAreEquals() {


        LinkedHashMap<String, OrderingType> map1 = new LinkedHashMap<String, OrderingType>();
        map1.put("attr1", OrderingType.ASC);
        map1.put("attr2", OrderingType.ASC);

        LinkedHashMap<String, OrderingType> map2 = new LinkedHashMap<String, OrderingType>();
        map2.put("attr1", OrderingType.ASC);
        map2.put("attr2", OrderingType.ASC);

        Assert.assertEquals(
                "Different sorted maps should not be equals",
                true,
                CollectionUtils.isMapOrderlyEquals(map1, map2));

    }

    @Test
    public void validateIsMapOrderlyEqualsWithFirstMapNull() {

        LinkedHashMap<String, OrderingType> map2 = new LinkedHashMap<String, OrderingType>();
        map2.put("attr1", OrderingType.ASC);
        map2.put("attr2", OrderingType.ASC);

        Assert.assertEquals(
                "Different sorted maps should not be equals",
                false,
                CollectionUtils.isMapOrderlyEquals(null, map2));

    }

    @Test
    public void validateIsMapOrderlyEqualsWithLastMapNull() {

        LinkedHashMap<String, OrderingType> map1 = new LinkedHashMap<String, OrderingType>();
        map1.put("attr1", OrderingType.ASC);
        map1.put("attr2", OrderingType.ASC);

        Assert.assertEquals(
                "Different sorted maps should not be equals",
                false,
                CollectionUtils.isMapOrderlyEquals(map1, null));

    }

    @Test
    public void validateIsMapOrderlyEqualsWithTwoNullMaps() {

        Assert.assertEquals(
                "Different sorted maps should not be equals",
                true,
                CollectionUtils.isMapOrderlyEquals(null, null));

    }

    /**
     * Validates using two maps with different sizes
     */
    @Test
    public void validateIsMapOrderlyEqualsMapsWithDifferentSizes() {

        LinkedHashMap<String, OrderingType> map1 = new LinkedHashMap<String, OrderingType>();
        map1.put("attr1", OrderingType.ASC);
        map1.put("attr2", OrderingType.ASC);

        LinkedHashMap<String, OrderingType> map2 = new LinkedHashMap<String, OrderingType>();
        map2.put("attr1", OrderingType.ASC);

        Assert.assertEquals(
                "Different sorted maps should not be equals",
                false,
                CollectionUtils.isMapOrderlyEquals(map1, map2));

    }

    /**
     * Resolved Migrations Sorting Happy day
     */
    @Test
    public void validateResolvedMigrationsSort() {

        List<ResolvedMigration> list = new ArrayList<>();
        ResolvedMigration a = new ResolvedMigration();
        a.setVersion(10);
        list.add(a);
        a = new ResolvedMigration();
        a.setVersion(2);
        list.add(a);
        a = new ResolvedMigration();
        a.setVersion(3);
        list.add(a);
        a = new ResolvedMigration();
        a.setVersion(1);
        list.add(a);

        List<ResolvedMigration> newList = CollectionUtils.sortResolvedMigrations(list);
        Assert.assertTrue("List should contain ordered resolved migrations", isResolvedMigrationListOrdered(newList));
    }

    /**
     * Validates if a resolved migrations list is ordered
     *
     * @param list
     * @return
     */
    private boolean isResolvedMigrationListOrdered(List<ResolvedMigration> list) {

        Integer lastNumber = -10;
        for (ResolvedMigration resolvedMigration : list) {

            if (resolvedMigration == null
                    || resolvedMigration.getVersion() == null) {
                continue;
            }

            if (lastNumber > resolvedMigration.getVersion()) {
                return false;
            }
            lastNumber = resolvedMigration.getVersion();
        }

        return true;
    }


    /**
     * Resolved Migrations Sorting Alternative Flow 1
     * First element null
     */
    @Test
    public void validateResolvedMigrationsSortFirstElementNull() {

        List<ResolvedMigration> list = new ArrayList<>();
        list.add(null);
        ResolvedMigration a = new ResolvedMigration();
        a.setVersion(3);
        list.add(a);
        a = new ResolvedMigration();
        a.setVersion(2);
        list.add(a);
        a = new ResolvedMigration();
        a.setVersion(1);
        list.add(a);

        List<ResolvedMigration> newList = CollectionUtils.sortResolvedMigrations(list);
        Assert.assertTrue("List should contain ordered resolved migrations", isResolvedMigrationListOrdered(newList));
    }

    /**
     * Resolved Migrations Sorting Alternative Flow 2
     * Three elements null
     */
    @Test
    public void validateResolvedMigrationsSortThreeElementsNull() {

        List<ResolvedMigration> list = new ArrayList<>();
        list.add(null);
        ResolvedMigration a = new ResolvedMigration();
        a.setVersion(1);
        list.add(a);
        list.add(null);

        List<ResolvedMigration> newList = CollectionUtils.sortResolvedMigrations(list);
        Assert.assertTrue("List should contain ordered resolved migrations", isResolvedMigrationListOrdered(newList));
    }

    /**
     * Resolved Migrations Sorting Alternative Flow 3
     * Two elements equals
     */
    @Test
    public void validateResolvedMigrationsSortTwoElementsEquals() {

        List<ResolvedMigration> list = new ArrayList<>();
        ResolvedMigration a = new ResolvedMigration();
        a.setVersion(1);
        list.add(a);

        a = new ResolvedMigration();
        a.setVersion(1);
        list.add(a);

        List<ResolvedMigration> newList = CollectionUtils.sortResolvedMigrations(list);
        Assert.assertTrue("List should contain ordered resolved migrations", isResolvedMigrationListOrdered(newList));
    }


}
