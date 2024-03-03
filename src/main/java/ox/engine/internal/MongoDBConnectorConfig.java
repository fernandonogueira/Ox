package ox.engine.internal;

import com.mongodb.MongoClient;

/**
 * MongoDBConnector Configuration.
 * <p/>
 * createCollectionIfDontExists is true by default
 */
public final class MongoDBConnectorConfig {

    private boolean createCollectionIfNotExists;
    private MongoClient mongo;
    private String databaseName;

    public static MongoDBConnectorConfig create() {
        return new MongoDBConnectorConfig();
    }

    private MongoDBConnectorConfig() {
        this.createCollectionIfNotExists = true;
    }

    public MongoDBConnectorConfig createCollectionIfDontExists(boolean value) {
        this.createCollectionIfNotExists = value;
        return this;
    }

    public MongoDBConnectorConfig setMongoClient(MongoClient mongo) {
        this.mongo = mongo;
        return this;
    }

    public boolean isCreateCollectionIfNotExists() {
        return createCollectionIfNotExists;
    }

    public MongoClient getMongo() {
        return mongo;
    }

    public MongoDBConnectorConfig setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
