package ox.engine.internal;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import ox.Configuration;
import ox.engine.exception.DatabaseNotFoundException;
import ox.engine.exception.InvalidCollectionException;
import ox.engine.exception.MissingCollectionException;
import ox.engine.exception.MissingMigrationHistoryCollectionException;
import ox.engine.structure.OrderingType;
import ox.utils.IndexUtils;
import ox.utils.logging.Logger;
import ox.utils.logging.Loggers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MongoDBConnector {

    private static final Logger LOG = Loggers.getLogger(MongoDBConnector.class);
    private final MongoDBConnectorConfig config;

    public MongoDBConnector(MongoDBConnectorConfig config) {
        LOG.info("[Ox] Configuring MongoDB Access...");
        this.config = config;
    }

    protected MongoDBConnectorConfig getConfig() {
        return config;
    }

    private MongoClient mongo() {
        return config.getMongo();
    }

    private MongoDatabase database() {
        return mongo().getDatabase(config.getDatabaseName());
    }

    public boolean collectionExists(String collectionName) {
        MongoIterable<String> collections = database().listCollectionNames();
        for (String collection : collections) {
            if (collection.equals(collectionName)) {
                return true;
            }
        }
        return false;
    }

    public Integer retrieveDatabaseCurrentVersion() {
        validateDatabaseNames();
        MongoDatabase db = database();

        if (collectionExists(config.getMigrationCollectionName())) {
            return getVersion();
        }

        if (!config.shouldCreateMigrationCollection()) {
            throw new MissingMigrationHistoryCollectionException("Versioning collection doesn't exists and auto collection create is set to false");
        }

        db.createCollection(config.getMigrationCollectionName(), new CreateCollectionOptions().capped(false));

        if (collectionExists(config.getMigrationCollectionName())) {
            createMigrateVersionsCollectionIndex();
            return getVersion();
        }

        throw new MissingMigrationHistoryCollectionException("Error trying to create collection.");
    }

    private void createMigrateVersionsCollectionIndex() {
        MongoDatabase database = database();
        MongoCollection<Document> collection = database.getCollection(config.getMigrationCollectionName());
        Bson objectIndex = new BasicDBObject(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, -1);
        collection.createIndex(objectIndex, new IndexOptions().unique(true));
    }

    /**
     * Validates if the selected database exists.
     * <p/>
     * If not exists, validates if is it possible to create it.
     */
    private void validateDatabaseNames() {
        MongoIterable<String> dbNamesIterable = config.getMongo().listDatabaseNames();
        List<String> dbNames = new ArrayList<>();
        try (MongoCursor<String> it = dbNamesIterable.iterator()) {
            while (it.hasNext()) {
                dbNames.add(it.next());
            }
        }
        if (dbNames.isEmpty()) {
            throw new DatabaseNotFoundException();
        }
        if (!dbNames.contains(config.getDatabaseName())
                && !config.shouldCreateMigrationCollection()) {
            throw new DatabaseNotFoundException("MongoDB Database not found and is not set for auto creation. Please create it and try again");
        }
    }

    private Integer getVersion() {
        MongoCollection<Document> collection = database().getCollection(config.getMigrationCollectionName());
        BasicDBObject o = new BasicDBObject();
        o.append(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, -1);
        FindIterable<Document> result = collection.find().sort(o).limit(1);

        try (MongoCursor<Document> it = result.iterator()) {
            if (!it.hasNext()) {
                return 0;
            }
            Document current = it.next();
            return (Integer) current.get(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE);
        }
    }

    public void executeCommand(OxAction action) {
        LOG.warn("[Ox] Executing action: {}", action);
        verifyAndCreateCollectionIfNecessary(action);
        action.runAction(this, config.getMongo(), config.getDatabaseName());
    }

    private void verifyAndCreateCollectionIfNecessary(OxAction action) {

        validateCollection(action);

        MongoDatabase db = database();
        boolean collectionExists = collectionExists(action.getCollection());

        if (!collectionExists) {
            if (config.shouldFailOnMissingCollection()) {
                throw new MissingCollectionException("failOnMissingCollection is set to true and collection does not exists. Collection: " + action.getCollection());
            } else {
                db.createCollection(action.getCollection(), new CreateCollectionOptions().capped(false));
            }
        }
    }

    private void validateCollection(OxAction action) {
        if (action == null || action.getCollection() == null) {
            throw new InvalidCollectionException();
        }
    }

    public void insertMigrationVersion(Integer version) {
        Document doc = new Document()
                .append(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, version)
                .append("date", new Date());

        MongoCollection<Document> db = database()
                .getCollection(config.getMigrationCollectionName());

        db.insertOne(doc);
    }

    public void removeMigrationVersion(Integer version) {
        Document doc = new Document()
                .append(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, version);

        database()
                .getCollection(config.getMigrationCollectionName())
                .deleteOne(doc);
    }

    /**
     * Verify if the collection contains an index
     * with the same name and different
     * attributes or attributes not equally ordered
     */
    public boolean verifyIfHasSameNameAndDifferentAttributes(
            Map<String, OrderingType> indexAttributes,
            String indexName,
            String collection) {

        ListIndexesIterable<Document> indexesIterable = database().getCollection(collection).listIndexes();
        List<Document> indexList = indexesIterable.into(new ArrayList<>());

        for (Document current : indexList) {
            String remoteIndexName = (String) current.get("name");
            if (!IndexUtils.verifyIfHasSameAttributesWithSameOrder(indexAttributes, current)
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

        ListIndexesIterable<Document> indexesIterable = database().getCollection(collection).listIndexes();
        List<Document> indexList = indexesIterable.into(new ArrayList<>());

        if (indexList.isEmpty()) {
            return false;
        }

        for (Document current : indexList) {

            if (IndexUtils.verifyIfHasSameAttributesWithSameOrder(indexAttributes, current)) {
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

    public void dropIndexByName(String collection, String indexName) {

        if (collection == null) {
            LOG.error("[Ox] Collection is null. Cannot drop Index.");
            return;
        }
        if (indexName == null) {
            LOG.error("[Ox] IndexName is null. Cannot drop Index.");
            return;
        }
        if (config.getDatabaseName() == null) {
            LOG.error("[Ox] database is null. Cannot drop Index.");
            return;
        }

        database().getCollection(collection).dropIndex(indexName);
    }

    public void createIndex(String collection, Bson indexDefinition, IndexOptions indexOptions) {
        LOG.info("Creating index... ");
        database().getCollection(collection).createIndex(indexDefinition, indexOptions);
    }

    public boolean verifyIfMigrateWasAlreadyExecuted(Integer version) {

        MongoCollection<Document> versionCollection = database().getCollection(config.getMigrationCollectionName());

        Document doc = new Document(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, version);
        long count = versionCollection.countDocuments(doc);

        return count > 0;
    }

    public MongoDatabase getMongoDatabase() {
        return database();
    }

    public void removeCollection(String collectionName) {
        getMongoDatabase().getCollection(collectionName).drop();
    }

}
