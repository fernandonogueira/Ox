package ox.engine.internal;

import com.mongodb.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.Configuration;
import ox.engine.exception.InvalidCollectionException;
import ox.engine.exception.InvalidMongoDatabaseConfiguration;
import ox.engine.structure.OrderingType;
import ox.utils.Faker;
import ox.utils.TestUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class MongoDBConnectorTest {

    private Mongo newMockedMongo() {
        return Mockito.mock(Mongo.class);
    }

    private static DB createMockedDB(boolean collectionExists) {

        DB db = Mockito.mock(DB.class);
        Mockito
                .when(db.collectionExists(Mockito.anyString()))
                .thenReturn(collectionExists).thenReturn(true);

        DBCollection dbCollection = Mockito.mock(DBCollection.class);

        Mockito
                .when((db.getCollection(Mockito.anyString()))).thenReturn(dbCollection);

        DBCursor dbCursor = Mockito.mock(DBCursor.class, "dbCursor1");

        Mockito.when(dbCollection.find()).thenReturn(dbCursor);
        Mockito.when(dbCursor.sort(Mockito.any(DBObject.class))).thenReturn(dbCursor);
        Mockito.when(dbCursor.limit(1)).thenReturn(dbCursor);
        Mockito.when(dbCursor.hasNext()).thenReturn(true).thenReturn(false);

        BasicDBObject o = new BasicDBObject();
        o.append(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, 1);

        Mockito.when(dbCursor.next()).thenReturn(o);

        return db;
    }

    /**
     * Validates if no database is found
     */
    @Test(expected = InvalidMongoDatabaseConfiguration.class)
    public void mongoDBConnectorNoDatabaseValidationTest() {
        TestUtils.newMongoDBConnector().retrieveDatabaseCurrentVersion();
    }

    /**
     * The configured database doesn't exists
     * and can't be created because automatic
     * creation is not set true
     */
    @Test(expected = InvalidMongoDatabaseConfiguration.class)
    public void mongoDBConnectorDatabaseNotFoundAndAutoCreationNotSetTest() {

        Mongo mockedMongo = Mockito.mock(Mongo.class);

        MongoDBConnector connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .setMongoClient(mockedMongo)
                .setDatabaseName("invalidDatabase")
                .createCollectionIfDontExists(false));

        ArrayList<String> dbNames = new ArrayList<>();
        dbNames.add("myDBName");

        Mockito
                .when(mockedMongo.getDatabaseNames())
                .thenReturn(dbNames);

        connector.retrieveDatabaseCurrentVersion();
    }

    /**
     * Validates the migration_versions get version method
     */
    @Test
    public void mongoDBConnectorGetSchemaVersionTest() {

        MongoDBConnector connector = TestUtils.newMongoDBConnector();
        List<String> dbNames = new ArrayList<>();

        dbNames.add(connector.getConfig().getDatabaseName());
        Mockito
                .when(connector.getConfig().getMongo().getDatabaseNames())
                .thenReturn(dbNames);

        DB db = createMockedDB(true);

        Mockito.when(connector.getConfig().getMongo().getDB(Mockito.anyString())).thenReturn(db);

        Integer version = connector.retrieveDatabaseCurrentVersion();
        Assert.assertEquals("DB Schema Version must be 1", Integer.valueOf(1), version);
    }

    /**
     * Tests the method that verifies
     * if the migration_versions collection exists
     */
    @Test
    public void mongoDBConnectorCreateSchemaVersionCollectionIfNotExists() {

        Mongo mockedMongo = Mockito.mock(Mongo.class);

        String fakeDB = Faker.fakeDBName();

        MongoDBConnector connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .setMongoClient(mockedMongo)
                .setDatabaseName(fakeDB)
                .createCollectionIfDontExists(true));

        List<String> dbNames = new ArrayList<>();
        dbNames.add(fakeDB);

        Mockito
                .when(connector.getConfig().getMongo().getDatabaseNames())
                .thenReturn(dbNames);

        DB db = createMockedDB(false);
        Mockito.when(connector.getConfig().getMongo().getDB(Mockito.anyString())).thenReturn(db);

        connector.retrieveDatabaseCurrentVersion();
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

        DB db = createMockedDB(false);
        Mockito.when(connector.getConfig().getMongo().getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.collectionExists(Mockito.anyString())).thenReturn(false);
        Mockito.when(action.getCollection()).thenReturn("aTestCollection");

        connector.executeCommand(action);
    }

    @Test(expected = InvalidCollectionException.class)
    public void executeInvalidCollectionCommandTest() {

        Mongo mockedMongo = Mockito.mock(Mongo.class);

        MongoDBConnector connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mockedMongo)
                .setDatabaseName(Faker.fakeDBName()));

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

        Mongo mockedMongo = Mockito.mock(Mongo.class);

        MongoDBConnectorConfig config = MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mockedMongo)
                .setDatabaseName("myDB");

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

        Mongo mockedMongo = Mockito.mock(Mongo.class);

        MongoDBConnectorConfig config = MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mockedMongo)
                .setDatabaseName(Faker.fakeDBName());

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

        Mongo mockedMongo = newMockedMongo();

        MongoDBConnectorConfig config = MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mockedMongo)
                .setDatabaseName("myDB");

        MongoDBConnector connector = new MongoDBConnector(config);

        LinkedHashMap<String, OrderingType> attrs = new LinkedHashMap<>();
        attrs.put("attr1", OrderingType.ASC);

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        List<DBObject> indexInfo = new ArrayList<>();

        Mockito.when(mockedMongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        Assert.assertTrue(!connector.verifyIfIndexExists(attrs, "myIndexTest", "myCollection"));
    }

    /**
     * Verifies if an index with same attributes exists.
     * <p/>
     * This should return true
     */
    @Test
    public void validateVerifyIfIndexWithTheSameNameExists() {

        Mongo mockedMongo = newMockedMongo();

        MongoDBConnectorConfig config = MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mockedMongo)
                .setDatabaseName("myDB");

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

        Mongo mockedMongo = Mockito.mock(Mongo.class);
        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(mockedMongo.getDB(Mockito.anyString())).thenReturn(db);

        MongoDBConnector connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .setMongoClient(mockedMongo)
                .createCollectionIfDontExists(true)
                .setDatabaseName("databaseName"));
        connector.dropIndexByName("collection", "indexName");
    }

    @Test
    public void createIndexTest() {

        Mongo mockedMongo = Mockito.mock(Mongo.class);
        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(mockedMongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        MongoDBConnector connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .setMongoClient(mockedMongo)
                .createCollectionIfDontExists(true)
                .setDatabaseName("databaseName"));

        connector.createIndex("coll", new BasicDBObject(), new BasicDBObject());
    }


}
