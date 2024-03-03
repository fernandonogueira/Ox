package ox.engine.internal;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ox.Configuration;
import ox.engine.exception.CouldNotCreateCollectionException;
import ox.engine.exception.InvalidCollectionException;
import ox.engine.exception.InvalidMongoDatabaseConfiguration;
import ox.engine.structure.OrderingType;
import ox.utils.CollectionUtils;

import java.util.*;

public class MongoDBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(MongoDBConnector.class);
    private final String databaseName;

    private final boolean createCollectionIfDontExists;

    private final MongoDBConnectorConfig config;

    public MongoDBConnector(MongoDBConnectorConfig config) {
        LOG.info("[Ox] Configuring MongoDB Access...");
        assert config != null;
        this.config = config;
        this.createCollectionIfDontExists = config.isCreateCollectionIfNotExists();
        this.databaseName = config.getDatabaseName();

        if (this.databaseName == null) {
            throw new InvalidMongoDatabaseConfiguration("Database name is null. Cannot proceed.");
        }
    }

    protected MongoDBConnectorConfig getConfig() {
        return config;
    }

    public Integer retrieveDatabaseCurrentVersion() {

        validateDatabaseNames();

        DB db = config.getMongo().getDB(databaseName);

        if (db.collectionExists(Configuration.SCHEMA_VERSION_COLLECTION_NAME)) {
            return getVersion(db);
        } else {
            if (createCollectionIfDontExists) {
                db.createCollection(Configuration.SCHEMA_VERSION_COLLECTION_NAME, new BasicDBObject().append("capped", false));
                if (db.collectionExists(Configuration.SCHEMA_VERSION_COLLECTION_NAME)) {
                    createMigrateVersionsCollectionIndex(db);
                    return getVersion(db);
                } else {
                    throw new CouldNotCreateCollectionException("Error trying to create collection.");
                }

            } else {
                throw new CouldNotCreateCollectionException("Versioning collection doesn't exists and auto collection create is set to false");
            }
        }
    }

    private void createMigrateVersionsCollectionIndex(DB db) {
        DBCollection collection = db.getCollection(Configuration.SCHEMA_VERSION_COLLECTION_NAME);
        BasicDBObject objectIndex = new BasicDBObject(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, -1);
        BasicDBObject indexOptions = new BasicDBObject("unique", true);
        collection.createIndex(objectIndex, indexOptions);
    }

    /**
     * Validates if the selected database exists.
     * <p/>
     * If not exists, validates if is it possible to create it.
     */
    private void validateDatabaseNames() {
        List<String> dbNames = config.getMongo().getDatabaseNames();

        if (dbNames == null || dbNames.isEmpty()) {
            throw new InvalidMongoDatabaseConfiguration("There is no existing database in this MongoDB. You must first create a new database");
        }

        if (!dbNames.contains(databaseName)
                && !createCollectionIfDontExists) {
            throw new InvalidMongoDatabaseConfiguration("MongoDB Database not found and is not set for auto creation. Please create it and try again");
        }

    }

    private Integer getVersion(DB db) {
        DBCollection collection = db.getCollection(Configuration.SCHEMA_VERSION_COLLECTION_NAME);
        BasicDBObject o = new BasicDBObject();
        o.append(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, -1);
        DBCursor result = collection.find().sort(o).limit(1);
        while (result.hasNext()) {
            DBObject current = result.next();
            return (Integer) current.get(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE);
        }
        return 0;
    }

    public void executeCommand(OxAction action) {
        LOG.warn("[Ox] Executing action: {}", action);
        verifyAndCreateCollectionIfNecessary(action);
        action.runAction(this, config.getMongo(), databaseName);
    }

    private void verifyAndCreateCollectionIfNecessary(OxAction action) {

        validateCollection(action);

        if (!config.getMongo().getDB(databaseName).collectionExists(action.getCollection())) {
            config.getMongo().getDB(databaseName)
                    .createCollection(
                            action.getCollection(),
                            new BasicDBObject()
                                    .append("capped", false)
                    );
        }
    }

    private void validateCollection(OxAction action) {
        if (action == null || action.getCollection() == null) {
            throw new InvalidCollectionException();
        }
    }

    public void insertMigrationVersion(Integer version) {
        BasicDBObject dbObject =
                new BasicDBObject(
                        Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE,
                        version);

        dbObject.append("date", new Date());

        config.getMongo()
                .getDB(databaseName)
                .getCollection(Configuration.SCHEMA_VERSION_COLLECTION_NAME)
                .insert(dbObject);
    }

    public void removeMigrationVersion(Integer version) {
        DBObject dbObject =
                new BasicDBObject(
                        Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE,
                        version);
        config.getMongo()
                .getDB(databaseName)
                .getCollection(Configuration.SCHEMA_VERSION_COLLECTION_NAME)
                .remove(dbObject);
    }

    /**
     * Verify if the collection contains an index
     * with the same name and different
     * attributes or attributes not equally ordered
     *
     * @param indexAttributes
     * @param indexName
     * @param collection
     * @return
     */
    public boolean verifyIfHasSameNameAndDifferentAttributes(
            Map<String, OrderingType> indexAttributes,
            String indexName,
            String collection) {

        List<DBObject> indexInfo = config.getMongo().getDB(databaseName).getCollection(collection).getIndexInfo();

        for (DBObject current : indexInfo) {
            String remoteIndexName = (String) current.get("name");
            if (!verifyIfHasSameAttributesWithSameOrder(indexAttributes, current)
                    && verifyIndexesHaveSameName(indexName, remoteIndexName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifies if an index already exists.
     * <p/>
     * If indexAttributes is null, this method will compare only the name of the indexes.
     * <p/>
     * <p/>
     * This will return true if an index with the same name OR the same attributes is found.
     *
     * @return true if the index already exists
     */
    protected boolean verifyIfIndexExists(
            Map<String, OrderingType> indexAttributes,
            String indexName,
            String collection) {

        List<DBObject> indexInfo = config.getMongo().getDB(databaseName).getCollection(collection).getIndexInfo();

        if (indexInfo.isEmpty()) {
            return false;
        }

        for (DBObject current : indexInfo) {

            if (verifyIfHasSameAttributesWithSameOrder(indexAttributes, current)) {
                return true;
            }

            String currentIndexName = (String) current.get("name");
            if (verifyIndexesHaveSameName(indexName, currentIndexName)) {
                return true;
            }

        }

        return false;
    }

    private boolean verifyIndexesHaveSameName(String indexName, String currentIndexName) {
        if (indexName.equals(currentIndexName)) {
            LOG.info("[Ox] Index already exists. (Same name: {})", indexName);
            return true;
        }
        return false;
    }

    private boolean verifyIfHasSameAttributesWithSameOrder(Map<String, OrderingType> indexAttributes, DBObject current) {
        if (indexAttributes != null) {
            Map<String, OrderingType> existingAttributes = identifyIndexAttributesAndOrdering(current);

            if (CollectionUtils.isMapOrderlyEquals(indexAttributes, existingAttributes)) {
                LOG.info("[Ox] Index already exists. (Same attributes. Same Order); {}", existingAttributes);
                return true;
            }
        }
        return false;
    }

    /**
     * Identify index attributes and ordering from a DBObject
     * <p/>
     * This DBObject is the DBObject that contains the info from an index retrieved from Database.
     *
     * @param current The DBObject retrieved from the IndexInfo List.
     * @return A map containing the attributes and ordering
     */
    private Map<String, OrderingType> identifyIndexAttributesAndOrdering(DBObject current) {
        Map<String, OrderingType> existingAttributes = new LinkedHashMap<>();
        DBObject indexAttrs = (DBObject) current.get("key");

        if (indexAttrs != null) {
            Set<String> attrKeySet = indexAttrs.keySet();

            for (String currentAttr : attrKeySet) {
                Object attrOrdering = indexAttrs.get(currentAttr);
                if (attrOrdering instanceof Integer) {
                    existingAttributes.put(currentAttr, attrOrdering.equals(1) ? OrderingType.ASC : OrderingType.DESC);
                } else if (attrOrdering instanceof String) {
                    existingAttributes.put(currentAttr, OrderingType.GEO_2DSPHERE);
                }
            }
        }
        return existingAttributes;
    }

    public void dropIndexByName(String collection, String indexName) {

        if (collection == null) {
            LOG.error("[Ox] Collection is null. Cannot drop Index.");
            return;
        }
        if (indexName == null) {
            LOG.error("[Ox] IndexName is null. Cannot drop Index.");
            return;
        }
        if (databaseName == null) {
            LOG.error("[Ox] database is null. Cannot drop Index.");
            return;
        }

        config.getMongo().getDB(databaseName).getCollection(collection).dropIndex(indexName);
    }

    public void createIndex(String collection, BasicDBObject indexDefinition, BasicDBObject indexOptions) {
        LOG.info("Creating index... ");
        config.getMongo().getDB(databaseName).getCollection(collection).createIndex(indexDefinition, indexOptions);
    }

    public boolean verifyIfMigrateWasAlreadyExecuted(Integer version) {

        DBCollection versionCollection = config.getMongo().getDB(databaseName).getCollection(Configuration.SCHEMA_VERSION_COLLECTION_NAME);
        versionCollection.setReadPreference(ReadPreference.primary());

        BasicDBObject dbObject = new BasicDBObject(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, version);

        long count = versionCollection.count(dbObject);

        return count > 0;
    }

    public DB getMongoDatabase() {
        return config.getMongo().getDB(databaseName);
    }

    public boolean verifyIfCollectionExists(String collectionName) {
        return getMongoDatabase().collectionExists(collectionName);
    }

    public void removeCollection(String collectionName) {
        getMongoDatabase().getCollection(collectionName).drop();
    }

}
