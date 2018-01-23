package ox.engine.internal;

import com.mongodb.Mongo;

/**
 * MongoDBConnector Configuration.
 * <p/>
 * createCollectionIfDontExists is true by default
 */
public final class MongoDBConnectorConfig {

    private boolean createCollectionIfDontExists;
    private Mongo mongo;
    private String databaseName;

    public static MongoDBConnectorConfig create() {
        return new MongoDBConnectorConfig();
    }

    private MongoDBConnectorConfig() {
        this.createCollectionIfDontExists = true;
    }

    public MongoDBConnectorConfig createCollectionIfDontExists(boolean value) {
        this.createCollectionIfDontExists = value;
        return this;
    }

    public MongoDBConnectorConfig setMongoClient(Mongo mongo) {
        this.mongo = mongo;
        return this;
    }

    public boolean isCreateCollectionIfDontExists() {
        return createCollectionIfDontExists;
    }

    public Mongo getMongo() {
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
