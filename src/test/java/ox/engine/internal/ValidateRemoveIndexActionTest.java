package ox.engine.internal;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidMigrateActionException;
import ox.utils.Faker;

@RunWith(MockitoJUnitRunner.class)
public class ValidateRemoveIndexActionTest {

    private MongoDBConnector newMongoDBConnector() {
        Mongo mockedMongo = Mockito.mock(Mongo.class);
        return new MongoDBConnector(MongoDBConnectorConfig.create()
                .setMongoClient(mockedMongo).setDatabaseName(Faker.fakeDBName()));
    }

    /**
     * IfExists enabled.
     * Index not found after looking into database
     *
     * @throws InvalidMigrateActionException
     */
    @Test
    public void testIndexRemovalWithIfExistsEnabledAndIndexNotFound() throws InvalidMigrateActionException {

        MongoDBConnector connector = newMongoDBConnector();

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDB(connector.getConfig().getDatabaseName())).thenReturn(db);
        Mockito.when(db.getCollection("testCollection")).thenReturn(collection);

        OxAction.removeIndex("someIndex")
                .setCollection("testCollection")
                .ifExists()
                .runAction(connector, connector.getConfig().getMongo(), "databaseName");
    }

    /**
     * IfExists enabled.
     * Index found after looking into database
     *
     * @throws InvalidMigrateActionException
     */
    @Test
    public void testIndexRemovalWithIfExistsEnabled() throws InvalidMigrateActionException {

        MongoDBConnector connector = newMongoDBConnector();

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(connector.getConfig().getMongo().getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        OxAction
                .removeIndex("someIndex")
                .setCollection("collectionName")
                .ifExists()
                .runAction(connector, connector.getConfig().getMongo(), "databaseName");

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

        MongoDBConnector mongoDBConnector = newMongoDBConnector();

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(mongoDBConnector.getConfig().getMongo().getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        OxAction
                .removeIndex("someIndex")
                .setCollection("collectionName")
                .runAction(mongoDBConnector, mongoDBConnector.getConfig().getMongo(), "databaseName");


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
