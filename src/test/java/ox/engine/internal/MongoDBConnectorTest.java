package ox.engine.internal;

import com.mongodb.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ox.Configuration;
import ox.engine.exception.InvalidCollectionException;
import ox.engine.exception.InvalidMongoDatabaseConfiguration;
import ox.engine.structure.OrderingType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Fernando Nogueira
 * @since 4/15/14 10:25 AM
 */
@RunWith(MockitoJUnitRunner.class)
public class MongoDBConnectorTest {

    @InjectMocks
    private MongoDBConnector connector;

    @Mock
    private Mongo mongo;

    /**
     * Validates if no database is found
     */
    @Test(expected = InvalidMongoDatabaseConfiguration.class)
    public void mongoDBConnectorNoDatabaseValidationTest() {

        connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .setMongoClient(mongo));

        connector.retrieveDatabaseCurrentVersion();
    }

    /**
     * The configured database doesn't exists
     * and can't be created because automatic
     * creation is not set true
     */
    @Test(expected = InvalidMongoDatabaseConfiguration.class)
    public void mongoDBConnectorDatabaseNotFoundAndAutoCreationNotSetTest() {

        connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .setMongoClient(mongo)
                .setDatabaseName("someDatabaseName")
                .createCollectionIfDontExists(false));

        ArrayList<String> dbNames = new ArrayList<>();
        dbNames.add("myDBName");

        Mockito
                .when(mongo.getDatabaseNames())
                .thenReturn(dbNames);

        connector.retrieveDatabaseCurrentVersion();
    }

    /**
     * Validates the migration_versions get version method
     */
    @Test
    public void mongoDBConnectorGetSchemaVersionTest() {
        connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .setMongoClient(mongo)
                .setDatabaseName("testCollection1"));

        List<String> dbNames = new ArrayList<>();
        dbNames.add("testCollection1");

        Mockito
                .when(mongo.getDatabaseNames())
                .thenReturn(dbNames);

        DB db = createMockedDB(true);

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);

        Integer version = connector.retrieveDatabaseCurrentVersion();
        Assert.assertEquals("DB Schema Version must be 1", new Integer(1), version);
    }

    /**
     * Tests the method that verifies
     * if the migration_versions collection exists
     */
    @Test
    public void mongoDBConnectorCreateSchemaVersionCollectionIfNotExists() {
        connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mongo)
                .setDatabaseName("testCollection1"));

        List<String> dbNames = new ArrayList<>();
        dbNames.add("testCollection1");

        Mockito
                .when(mongo.getDatabaseNames())
                .thenReturn(dbNames);

        DB db = createMockedDB(false);
        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);

        connector.retrieveDatabaseCurrentVersion();
    }

    /**
     * Validates the executeCommand method.
     * <p/>
     * This is the method that executes actions
     */
    @Test
    public void executeCommandTest() {
        connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mongo)
                .setDatabaseName("testDatabase1"));

        OxAction action = Mockito.mock(OxAction.class);

        DB db = createMockedDB(false);
        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.collectionExists(Mockito.anyString())).thenReturn(false);
        Mockito.when(action.getCollection()).thenReturn("aTestCollection");

        connector.executeCommand(action);
    }

    @Test(expected = InvalidCollectionException.class)
    public void executeInvalidCollectionCommandTest() {
        connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mongo)
                .setDatabaseName("testDatabase1"));

        OxAction action = Mockito.mock(OxAction.class);

        DB db = createMockedDB(false);
        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.collectionExists(Mockito.anyString())).thenReturn(false);
        Mockito.when(action.getCollection()).thenReturn(null);

        connector.executeCommand(action);
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
     * Verifies if an index with same attributes exists.
     * <p/>
     * This should return true
     */
    @Test
    public void verifyIfAn2dsphereIndexWithSameAttributesAlreadyExistsTest() {

        MongoDBConnectorConfig config = MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mongo)
                .setDatabaseName("myDB");

        connector = new MongoDBConnector(config);

        LinkedHashMap<String, OrderingType> attrs = new LinkedHashMap<>();
        attrs.put("attr1", OrderingType.GEO_2DSPHERE);

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        List<DBObject> indexInfo = new ArrayList<>();

        BasicDBObject dbObjectKeys = new BasicDBObject("attr1", "2dsphere");
        BasicDBObject dbObject = new BasicDBObject("key", dbObjectKeys);

        indexInfo.add(dbObject);

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        Assert
                .assertTrue(
                        connector
                                .verifyIfIndexExists(
                                        attrs,
                                        "myIndexTest",
                                        "myCollection")
                );
    }

    /**
     * Verifies if an index with same attributes exists.
     * <p/>
     * This should return true
     */
    @Test
    public void verifyIfAnIndexWithSameAttributesAlreadyExistsTest() {

        MongoDBConnectorConfig config = MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mongo)
                .setDatabaseName("myDB");

        connector = new MongoDBConnector(config);

        LinkedHashMap<String, OrderingType> attrs = new LinkedHashMap<>();
        attrs.put("attr1", OrderingType.ASC);

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        List<DBObject> indexInfo = new ArrayList<>();

        BasicDBObject dbObjectKeys = new BasicDBObject("attr1", 1);
        BasicDBObject dbObject = new BasicDBObject("key", dbObjectKeys);

        indexInfo.add(dbObject);

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        Assert
                .assertTrue(
                        connector
                                .verifyIfIndexExists(
                                        attrs,
                                        "myIndexTest",
                                        "myCollection")
                );
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

        MongoDBConnectorConfig config = MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mongo)
                .setDatabaseName("myDB");

        connector = new MongoDBConnector(config);

        LinkedHashMap<String, OrderingType> attrs = new LinkedHashMap<>();
        attrs.put("attr1", OrderingType.ASC);

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);
        List<DBObject> indexInfo = new ArrayList<>();

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        Assert
                .assertTrue(
                        !connector
                                .verifyIfIndexExists(
                                        attrs,
                                        "myIndexTest",
                                        "myCollection")
                );
    }

    /**
     * Verifies if an index with same attributes exists.
     * <p/>
     * This should return true
     */
    @Test
    public void validateVerifyIfIndexWithTheSameNameExists() {

        MongoDBConnectorConfig config = MongoDBConnectorConfig
                .create()
                .createCollectionIfDontExists(true)
                .setMongoClient(mongo)
                .setDatabaseName("myDB");

        connector = new MongoDBConnector(config);

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

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.getIndexInfo()).thenReturn(indexInfo);

        Assert
                .assertTrue(
                        connector
                                .verifyIfIndexExists(
                                        attrs,
                                        "myIndexTest",
                                        "myCollection")
                );
    }

    /**
     * Validates the insertion of a
     * new migration version into migration_versions collection
     */
    @Test
    public void insertMigrationVersionTest() {

        connector = new MongoDBConnector(MongoDBConnectorConfig.create()
                .setMongoClient(mongo));

        DB db = Mockito.mock(DB.class);

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        connector.insertMigrationVersion(10);
    }

    /**
     * Validates the removal of a
     * migration version from migration_versions collection
     */
    @Test
    public void removeMigrationVersionTest() {

        connector = new MongoDBConnector(MongoDBConnectorConfig.create()
                .setMongoClient(mongo));

        DB db = Mockito.mock(DB.class);

        DBCollection collection = Mockito.mock(DBCollection.class);
        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        connector.removeMigrationVersion(10);
    }

    @Test
    public void dropIndexByNameTest() {
        connector.dropIndexByName("collection", "indexName");
    }

    @Test
    public void dropIndexByNameCollectionNullTest() {
        connector.dropIndexByName(null, "indexName");
    }

    @Test
    public void dropIndexByNameIndexNullTest() {
        connector.dropIndexByName("collection", null);
    }

    @Test
    public void dropIndexByNameDatabaseNotNullTest() {

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);

        connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .setMongoClient(mongo)
                .createCollectionIfDontExists(true)
                .setDatabaseName("databaseName"));
        connector.dropIndexByName("collection", "indexName");
    }

    @Test
    public void createIndexTest() {
        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        connector = new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .setMongoClient(mongo)
                .createCollectionIfDontExists(true)
                .setDatabaseName("databaseName"));

        connector.createIndex("coll", new BasicDBObject(), new BasicDBObject());
    }


}