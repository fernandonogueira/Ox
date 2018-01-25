package ox.engine;

import org.junit.Assert;
import org.junit.Test;
import ox.engine.exception.InvalidAttributeException;
import ox.engine.internal.CreateIndexAction;
import ox.engine.internal.OxAction;
import ox.engine.internal.RemoveCollectionAction;
import ox.engine.structure.OrderingType;

public class OxActionsTest {

    /**
     * A created migrateaction should have the properties that I set, right?
     */
    @Test
    public void createIndexCriteriaTest() {
        OxAction action = OxAction
                .createIndex("IndexName")
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.ASC)
                .addAttribute("attr2", OrderingType.ASC);

        Assert.assertTrue("OxAction must be valid", validateCreateIndexAction((CreateIndexAction) action));
    }

    /**
     * A Null value must not be accepted
     */
    @Test(expected = InvalidAttributeException.class)
    public void createIndexCriteriaNullAttributeValidationTest() {
        OxAction
                .createIndex("IndexName")
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.ASC)
                .addAttribute(null, OrderingType.ASC);
    }

    /**
     * Is it possible to add an index with OrderingType = DESC?
     */
    @Test
    public void createIndexDescOrderingTest() {
        OxAction
                .createIndex("IndexName")
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.DESC);
    }

    @Test
    public void createIndexWithRecreateIfAlreadyExists() {
        CreateIndexAction action = OxAction
                .createIndex("myIndex")
                .setCollection("myColl")
                .addAttribute("attr1", OrderingType.GEO_2DSPHERE)
                .recreateIfNotEquals();

        Assert.assertTrue(validateCreateIndexAction(action));
    }

    /**
     * A created OxAction should have the properties that I set, right?
     */
    @Test
    public void removeIndexCriteriaTest() {
        OxAction action = OxAction
                .removeCollection("myCollection");

        Assert.assertTrue("OxAction must be valid", validateRemoveCollectionAction((RemoveCollectionAction) action));
    }

    private boolean validateRemoveCollectionAction(RemoveCollectionAction action) {
        return action != null
                && action.getCollection() != null;
    }

    private boolean validateCreateIndexAction(CreateIndexAction action) {
        return action != null
                && action.getAttributes() != null
                && action.getIndexName() != null
                && !action.getAttributes().isEmpty();
    }

}
