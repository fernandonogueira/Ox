package ox.engine.internal;

import com.mongodb.*;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidCollectionException;
import ox.engine.structure.OrderingType;
import ox.utils.Faker;
import ox.utils.TestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class MongoDBConnectorTest {

    private MongoClient newMockedMongo() {
        return Mockito.mock(MongoClient.class);
    }

    private static MongoDatabase createMockedDB(List<String> existingCollections) {

        MongoDatabase db = Mockito.mock(MongoDatabase.class);

        MongoIterable<String> iterable = Mockito.mock(MongoIterable.class);
        MongoCursor<String> cursor = Mockito.mock(MongoCursor.class);

        Mockito.when(db.listCollectionNames()).thenReturn(iterable);
        Mockito.when(iterable.iterator()).thenReturn(cursor);

        if (!existingCollections.isEmpty()) {
            Mockito.when(cursor.hasNext()).thenReturn(true).thenReturn(false);
        } else {
            Mockito.when(cursor.hasNext()).thenReturn(false);
        }

        return db;
    }

    /**
     * Validates the executeCommand method.
     * <p/>
     * This is the method that executes actions
     */
    @Test
    public void executeCommandTest() {

        MongoDBConnector connector = TestUtils.newMongoDBConnector();

        OxAction action = Mockito.mock(OxAction.class);

        MongoDatabase db = createMockedDB(Collections.emptyList());
        Mockito.when(connector.getConfig().getMongo().getDatabase(Mockito.anyString())).thenReturn(db);
        Mockito.when(action.getCollection()).thenReturn("aTestCollection");

        connector.executeCommand(action);
    }

    @Test(expected = InvalidCollectionException.class)
    public void executeInvalidCollectionCommandTest() {

        MongoClient mockedMongo = Mockito.mock(MongoClient.class);

        MongoDBConnector connector = new MongoDBConnector(
                MongoDBConnectorConfig.builder()
                        .setMongoClient(mockedMongo)
                        .setDatabaseName(Faker.fakeDBName())
                        .build());

        OxAction action = Mockito.mock(OxAction.class);
        Mockito.when(action.getCollection()).thenReturn(null);

        connector.executeCommand(action);
    }

    /**
     * Verifies if an index with same attributes exists.
     * <p/>
     * This should return true
     */
    @Test
    public void verifyIfAn2dsphereIndexWithSameAttributesAlreadyExistsTest() {

        MongoClient mockedMongo = Mockito.mock(MongoClient.class);

        MongoDBConnectorConfig config = MongoDBConnectorConfig.builder()
                .setMongoClient(mockedMongo)
                .setDatabaseName("myDB").build();

        MongoDBConnector connector = new MongoDBConnector(config);

        LinkedHashMap<String, OrderingType> attrs = new LinkedHashMap<>();
        attrs.put("attr1", OrderingType.GEO_2DSPHERE);

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        List<DBObject> indexInfo = new ArrayList<>();

        BasicDBObject dbObjectKeys = new BasicDBObject("attr1", "2dsphere");
        BasicDBObject dbObject = new BasicDBObject("key", dbObjectKeys);

        indexInfo.add(dbObject);

        Mockito.when(mockedMongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        Assert.assertTrue(connector.verifyIfIndexExists(attrs, "myIndexTest", "myCollection"));
    }

    /**
     * Verifies if an index with same attributes exists.
     * <p/>
     * This should return true
     */
    @Test
    public void verifyIfAnIndexWithSameAttributesAlreadyExistsTest() {

        MongoClient mockedMongo = Mockito.mock(MongoClient.class);

        MongoDBConnectorConfig config = MongoDBConnectorConfig.builder()
                .setMongoClient(mockedMongo)
                .setDatabaseName(Faker.fakeDBName()).build();

        MongoDBConnector connector = new MongoDBConnector(config);

        LinkedHashMap<String, OrderingType> attrs = new LinkedHashMap<>();
        attrs.put("attr1", OrderingType.ASC);

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        List<DBObject> indexInfo = new ArrayList<>();

        BasicDBObject dbObjectKeys = new BasicDBObject("attr1", 1);
        BasicDBObject dbObject = new BasicDBObject("key", dbObjectKeys);

        indexInfo.add(dbObject);

        Mockito.when(mockedMongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        Assert.assertTrue(connector.verifyIfIndexExists(attrs, "myIndexTest", "myCollection"));
    }

    /**
     * Validates the veryIfIndexExists() method.
     * <p/>
     * This tests expects that there the collection has no index
     * <p/>
     * <p/>
     * This should return false
     */
    @Test
    public void validateVerifyIfIndexExistsWhenTheCollectionHasNoIndex() {

        MongoClient mockedMongo = newMockedMongo();

        MongoDBConnectorConfig config = MongoDBConnectorConfig.builder()
                .setMongoClient(mockedMongo)
                .setDatabaseName("myDB").build();

        MongoDBConnector connector = new MongoDBConnector(config);

        LinkedHashMap<String, OrderingType> attrs = new LinkedHashMap<>();
        attrs.put("attr1", OrderingType.ASC);

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        List<DBObject> indexInfo = new ArrayList<>();

        Mockito.when(mockedMongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        Assert.assertFalse(connector.verifyIfIndexExists(attrs, "myIndexTest", "myCollection"));
    }

    /**
     * Verifies if an index with same attributes exists.
     * <p/>
     * This should return true
     */
    @Test
    public void validateVerifyIfIndexWithTheSameNameExists() {

        MongoClient mockedMongo = newMockedMongo();

        MongoDBConnectorConfig config = MongoDBConnectorConfig.builder()
                .setMongoClient(mockedMongo)
                .setDatabaseName("myDB").build();

        MongoDBConnector connector = new MongoDBConnector(config);

        LinkedHashMap<String, OrderingType> attrs = new LinkedHashMap<>();
        attrs.put("attr1", OrderingType.ASC);

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        List<DBObject> indexInfo = new ArrayList<>();

        BasicDBObject dbObjectKeys = new BasicDBObject("attr1", 1);
        dbObjectKeys.append("attr2Geo", "2dsphere");

        BasicDBObject dbObject = new BasicDBObject("key", dbObjectKeys);
        dbObject.append("name", "myIndexTest");

        indexInfo.add(dbObject);

        Mockito.when(mockedMongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        Assert.assertTrue(connector.verifyIfIndexExists(attrs, "myIndexTest", "myCollection"));
    }

    /**
     * Validates the insertion of a
     * new migration version into migration_versions collection
     */
    @Test
    public void insertMigrationVersionTest() {

        MongoDBConnector connector = TestUtils.newMongoDBConnector();

        DB db = Mockito.mock(DB.class);

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        connector.insertMigrationVersion(10);
    }

    /**
     * Validates the removal of a
     * migration version from migration_versions collection
     */
    @Test
    public void removeMigrationVersionTest() {

        MongoDBConnector connector = TestUtils.newMongoDBConnector();
        DB db = Mockito.mock(DB.class);

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        connector.removeMigrationVersion(10);
    }

    @Test
    public void dropIndexByNameTest() {
        MongoDBConnector connector = TestUtils.newMongoDBConnector();

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        connector.dropIndexByName("collection", "indexName");
    }

    @Test
    public void dropIndexByNameCollectionNullTest() {
        MongoDBConnector connector = TestUtils.newMongoDBConnector();
        connector.dropIndexByName(null, "indexName");
    }

    @Test
    public void dropIndexByNameIndexNullTest() {
        MongoDBConnector connector = TestUtils.newMongoDBConnector();
        connector.dropIndexByName("collection", null);
    }

    @Test
    public void dropIndexByNameDatabaseNotNullTest() {

        MongoClient mockedMongo = Mockito.mock(MongoClient.class);
        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(mockedMongo.getDB(Mockito.anyString())).thenReturn(db);

        MongoDBConnector connector = new MongoDBConnector(
                MongoDBConnectorConfig
                        .builder()
                        .setMongoClient(mockedMongo)
                        .setDatabaseName("databaseName")
                        .build());
        connector.dropIndexByName("collection", "indexName");
    }

    @Test
    public void createIndexTest() {

        MongoClient mockedMongo = Mockito.mock(MongoClient.class);
        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(mockedMongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        MongoDBConnector connector = new MongoDBConnector(
                MongoDBConnectorConfig.builder()
                        .setMongoClient(mockedMongo)
                        .setDatabaseName("databaseName")
                        .build());

        connector.createIndex("coll", new BasicDBObject(), new BasicDBObject());
    }

}
