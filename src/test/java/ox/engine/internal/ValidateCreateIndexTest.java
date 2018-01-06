package ox.engine.internal;

import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.structure.OrderingType;
import com.mongodb.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author Fernando Nogueira
 * @since 4/16/14 9:22 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateCreateIndexTest {

    @InjectMocks
    private CreateIndexAction createIndexAction;

    @Mock
    private MongoClient mongo;

    @Mock
    private MigrationEnvironment env;

    @Mock
    private DB db;

    @Mock
    private DBCollection collection;

    @Mock
    private MongoDBConnector connector;

    /**
     * Tries to create a index that doesn't exists yet.
     */
    @Test
    public void createIndexIfIndexDoesntExistsTest() {

        Mockito.when(mongo.getDB("myDatabase")).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        MigrateAction
                .createIndex("myIndex")
                .ifNotExists()
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.ASC)
                .unique()
                .dropDups()
                .runAction(connector, mongo, "myDatabase");


    }

    /**
     * Tries to create a index that doesn't exists yet.
     */
    @Test
    public void createIndexIfIndexDoesntExistsUsingDescOrderingTest() {

        Mockito.when(mongo.getDB("myDatabase")).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        MigrateAction
                .createIndex("myIndex")
                .ifNotExists()
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.DESC)
                .unique()
                .runAction(connector, mongo, "myDatabase");


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

        Mockito.when(mongo.getDB("myDatabase")).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        ArrayList<DBObject> indexInfo = new ArrayList<DBObject>();


        BasicDBObject keys = new BasicDBObject();
        keys.append("attr1", 1);


        BasicDBObject o = new BasicDBObject();
        o.append("key", keys);
        o.append("name", "someIndexName");

        indexInfo.add(o);

        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        Mockito.when(
                connector
                        .verifyIfIndexExists(
                                Mockito.anyMap(),
                                Mockito.anyString(),
                                Mockito.anyString()))
                .thenReturn(true);

        MigrateAction
                .createIndex("myIndex")
                .ifNotExists()
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.DESC)
                .runAction(connector, mongo, "myDatabase");

    }

    /**
     * Tries to create a index that already exists.
     * (Same name)
     * The index must not be created. This action should be ignored.
     */
    @Test
    public void createIndexIfIndexAlreadyExistsWithSameNameTest() {

        Mockito.when(mongo.getDB("myDatabase")).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        ArrayList<DBObject> indexInfo = new ArrayList<DBObject>();

        BasicDBObject keys = new BasicDBObject();
        keys.append("attr2", 1);

        BasicDBObject o = new BasicDBObject();
        o.append("key", keys);
        o.append("name", "myIndex");

        indexInfo.add(o);

        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        MigrateAction
                .createIndex("myIndex")
                .ifNotExists()
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.ASC)
                .runAction(connector, mongo, "myDatabase");

    }

    /**
     * Tries to create a index an invalid index
     * (Same name)
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void createAnInvalidIndexTest() throws InvalidMigrateActionException {

        Mockito.when(mongo.getDB("myDatabase")).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        ArrayList<DBObject> indexInfo = new ArrayList<DBObject>();

        BasicDBObject keys = new BasicDBObject();
        keys.append("attr2", 1);

        BasicDBObject o = new BasicDBObject();
        o.append("key", keys);
        o.append("name", "myIndex");

        indexInfo.add(o);

        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        MigrateAction
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
        Mockito.when(mongo.getDB("myDatabase")).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        ArrayList<DBObject> indexInfo = new ArrayList<DBObject>();

        BasicDBObject keys = new BasicDBObject();
        keys.append("attr2", 1);

        BasicDBObject o = new BasicDBObject();
        o.append("key", keys);
        o.append("name", "myIndex");

        indexInfo.add(o);

        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        MigrateAction
                .createIndex("myIndex")
                .setCollection("myLittleCollection")
                .ifNotExists()
                .addAttribute("attr1", OrderingType.ASC)
                .addAttribute("attr2", OrderingType.ASC)
                .markAsTTL(TimeUnit.DAYS.toSeconds(90))
                .execute(env);
    }

}
