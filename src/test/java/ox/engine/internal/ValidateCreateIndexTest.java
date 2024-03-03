package ox.engine.internal;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.structure.OrderingType;
import ox.utils.TestUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class ValidateCreateIndexTest {

    /**
     * Tries to create a index that doesn't exists yet.
     */
    @Test
    public void createIndexIfIndexDoesntExistsTest() {

        MongoDBConnector connector = TestUtils.newMongoDBConnector();

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(connector.getConfig().getMongo().getDB(connector.getConfig().getDatabaseName())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        OxAction
                .createIndex("myIndex")
                .ifNotExists()
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.ASC)
                .unique()
                .dropDups()
                .runAction(connector, connector.getConfig().getMongo(), connector.getConfig().getDatabaseName());

    }

    /**
     * Tries to create a index that doesn't exists yet.
     */
    @Test
    public void createIndexIfIndexDoesntExistsUsingDescOrderingTest() {

        MongoDBConnector connector = TestUtils.newMongoDBConnector();

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(connector.getConfig().getMongo().getDB(connector.getConfig().getDatabaseName())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        OxAction
                .createIndex("myIndex")
                .ifNotExists()
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.DESC)
                .unique()
                .runAction(connector, connector.getConfig().getMongo(), connector.getConfig().getDatabaseName());


    }

    /**
     * Tries to create a index that already exists.
     * (Same attributes)
     * <p/>
     * The index must not be created. This action should be ignored.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void createIndexIfIndexAlreadyExistsTest() {

        MongoDBConnector connector = TestUtils.newMongoDBConnector();

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(connector.getConfig().getMongo().getDB(connector.getConfig().getDatabaseName())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        ArrayList<DBObject> indexInfo = new ArrayList<DBObject>();

        BasicDBObject keys = new BasicDBObject();
        keys.append("attr1", 1);

        BasicDBObject o = new BasicDBObject();
        o.append("key", keys);
        o.append("name", "someIndexName");

        indexInfo.add(o);

        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        OxAction
                .createIndex("myIndex")
                .ifNotExists()
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.DESC)
                .runAction(connector, connector.getConfig().getMongo(), connector.getConfig().getDatabaseName());

    }

    /**
     * Tries to create a index that already exists.
     * (Same name)
     * The index must not be created. This action should be ignored.
     */
    @Test
    public void createIndexIfIndexAlreadyExistsWithSameNameTest() {

        MongoDBConnector connector = TestUtils.newMongoDBConnector();
        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDB(connector.getConfig().getDatabaseName())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        ArrayList<DBObject> indexInfo = new ArrayList<DBObject>();

        BasicDBObject keys = new BasicDBObject();
        keys.append("attr2", 1);

        BasicDBObject o = new BasicDBObject();
        o.append("key", keys);
        o.append("name", "myIndex");

        indexInfo.add(o);

        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        OxAction
                .createIndex("myIndex")
                .ifNotExists()
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.ASC)
                .runAction(connector, connector.getConfig().getMongo(), connector.getConfig().getDatabaseName());

    }

    /**
     * Tries to create a index an invalid index
     * (Same name)
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void createAnInvalidIndexTest() throws InvalidMigrateActionException {

        OxEnvironment env = new OxEnvironment();

        ArrayList<DBObject> indexInfo = new ArrayList<DBObject>();

        BasicDBObject keys = new BasicDBObject();
        keys.append("attr2", 1);

        BasicDBObject o = new BasicDBObject();
        o.append("key", keys);
        o.append("name", "myIndex");

        indexInfo.add(o);

        OxAction
                .createIndex("myIndex")
                .ifNotExists()
                .addAttribute("attr1", OrderingType.ASC)
                .execute(env);

    }

    /**
     * Tries to create an invalid TTL index (with more than 1 attribute)
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void createAnInvalidTTLIndex() throws InvalidMigrateActionException {

        OxEnvironment env = new OxEnvironment();
        OxAction
                .createIndex("myIndex")
                .setCollection("myRandomCollection")
                .ifNotExists()
                .addAttribute("attr1", OrderingType.ASC)
                .addAttribute("attr2", OrderingType.ASC)
                .markAsTTL(TimeUnit.DAYS.toSeconds(90))
                .execute(env);
    }

}
