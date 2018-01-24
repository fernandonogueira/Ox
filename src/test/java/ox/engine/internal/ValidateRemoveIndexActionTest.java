package ox.engine.internal;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ox.engine.exception.InvalidMigrateActionException;

/**
 * @author Fernando Nogueira
 * @since 4/20/14 1:51 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateRemoveIndexActionTest {

    @Mock
    private MongoDBConnector mongoDBConnector;

    @Mock
    private Mongo mongo;

    @Mock
    private DB db;

    @Mock
    private DBCollection collection;

    /**
     * IfExists enabled.
     * Index not found after looking into database
     *
     * @throws InvalidMigrateActionException
     */
    @Test
    public void testIndexRemovalWithIfExistsEnabledAndIndexNotFound() throws InvalidMigrateActionException {
        OxAction.removeIndex("someIndex")
                .setCollection("collectionName")
                .ifExists()
                .runAction(mongoDBConnector, mongo, "databaseName");
    }

    /**
     * IfExists enabled.
     * Index found after looking into database
     *
     * @throws InvalidMigrateActionException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testIndexRemovalWithIfExistsEnabled() throws InvalidMigrateActionException {

        Mockito.when(
                mongoDBConnector.verifyIfIndexExists(
                        Mockito.anyMap(),
                        Mockito.anyString(),
                        Mockito.anyString())
        )
                .thenReturn(true);

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        OxAction
                .removeIndex("someIndex")
                .setCollection("collectionName")
                .ifExists()
                .runAction(mongoDBConnector, mongo, "databaseName");


    }

    /**
     * IfExists DISABLED.
     * Index found after looking into database
     *
     * @throws InvalidMigrateActionException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testIndexRemovalWithoutIfExistsEnabled() throws InvalidMigrateActionException {

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        OxAction
                .removeIndex("someIndex")
                .setCollection("collectionName")
                .runAction(mongoDBConnector, mongo, "databaseName");


    }

    /**
     * No collection set
     *
     * @throws InvalidMigrateActionException
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void validateRemoveIndexAction() throws InvalidMigrateActionException {
        OxAction
                .removeIndex("someIndex")
                .validateAction();
    }

    /**
     * No index name set
     *
     * @throws InvalidMigrateActionException
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void validateRemoveIndexActionWithoutIndexName() throws InvalidMigrateActionException {
        OxAction
                .removeIndex(null)
                .validateAction();
    }

    /**
     * Happy day!
     *
     * @throws InvalidMigrateActionException
     */
    @Test
    public void validateRemoveIndexActionHappyDay() throws InvalidMigrateActionException {
        OxAction
                .removeIndex("correctIndexName")
                .setCollection("collectionSet")
                .validateAction();
    }

}