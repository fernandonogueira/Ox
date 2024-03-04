package ox.engine.internal;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidCollectionException;
import ox.engine.structure.OrderingType;
import ox.utils.Faker;
import ox.utils.TestUtils;

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

        Document dbObjectKeys = new Document("attr1", "2dsphere");
        Document dbObject = new Document("key", dbObjectKeys);

        ListIndexesIterable<Document> indexInfo = Mockito.mock(ListIndexesIterable.class);
        MongoDatabase db = Mockito.mock(MongoDatabase.class);
        MongoCollection collection = Mockito.mock(MongoCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDatabase(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.listIndexes()).thenReturn(indexInfo);
        Mockito.when(indexInfo.into(Mockito.anyList())).thenReturn(Collections.singletonList(dbObject));

        boolean indexExists = connector
                .verifyIfIndexExists(attrs, "myIndexTest", "myCollection");

        Assert.assertTrue(indexExists);
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

        MongoDatabase db = Mockito.mock(MongoDatabase.class);
        MongoCollection collection = Mockito.mock(MongoCollection.class);
        ListIndexesIterable indexInfo = Mockito.mock(ListIndexesIterable.class);

        Document dbObjectKeys = new Document("attr1", 1);
        Document dbObject = new Document("key", dbObjectKeys);

        Mockito.when(mockedMongo.getDatabase(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.listIndexes()).thenReturn(indexInfo);
        Mockito.when(indexInfo.into(Mockito.anyList())).thenReturn(Collections.singletonList(dbObject));

        boolean indexExists = connector.verifyIfIndexExists(attrs, "myIndexTest", "myCollection");

        Assert.assertTrue(indexExists);
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

        ListIndexesIterable<Document> indexInfo = Mockito.mock(ListIndexesIterable.class);
        MongoDatabase db = Mockito.mock(MongoDatabase.class);
        MongoCollection collection = Mockito.mock(MongoCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDatabase(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.listIndexes()).thenReturn(indexInfo);
        Mockito.when(indexInfo.into(Mockito.anyList())).thenReturn(Collections.emptyList());

        boolean indexExists = connector.verifyIfIndexExists(attrs, "myIndexTest", "myCollection");
        Assert.assertFalse(indexExists);
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

        Document dbObjectKeys = new Document("attr1", 1)
                .append("attr2Geo", "2dsphere");

        Document dbObject = new Document("key", dbObjectKeys)
                .append("name", "myIndexTest");

        ListIndexesIterable<Document> indexInfo = Mockito.mock(ListIndexesIterable.class);
        MongoDatabase db = Mockito.mock(MongoDatabase.class);
        MongoCollection collection = Mockito.mock(MongoCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDatabase(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.listIndexes()).thenReturn(indexInfo);
        Mockito.when(indexInfo.into(Mockito.anyList())).thenReturn(Collections.singletonList(dbObject));

        boolean indexExists = connector.verifyIfIndexExists(attrs, "myIndexTest", "myCollection");

        Assert.assertTrue(indexExists);
    }

    /**
     * Validates the insertion of a
     * new migration version into migration_versions collection
     */
    @Test
    public void insertMigrationVersionTest() {

        MongoDBConnector connector = TestUtils.newMongoDBConnector();

        MongoDatabase db = Mockito.mock(MongoDatabase.class);
        MongoCollection collection = Mockito.mock(MongoCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDatabase(Mockito.anyString())).thenReturn(db);
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
        MongoDatabase db = Mockito.mock(MongoDatabase.class);
        MongoCollection collection = Mockito.mock(MongoCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDatabase(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        connector.removeMigrationVersion(10);
    }

    @Test
    public void dropIndexByNameTest() {
        MongoDBConnector connector = TestUtils.newMongoDBConnector();

        MongoDatabase db = Mockito.mock(MongoDatabase.class);
        MongoCollection collection = Mockito.mock(MongoCollection.class);
        Mockito.when(connector.getConfig().getMongo().getDatabase(Mockito.anyString())).thenReturn(db);
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

}
