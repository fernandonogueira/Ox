package ox.engine.internal;

import com.mongodb.MongoClient;
import ox.engine.OxConfig;

/**
 * MongoDBConnector Configuration.
 * <p/>
 * createCollectionIfDontExists is true by default
 */
public final class MongoDBConnectorConfig {

    private boolean createMigrationCollection = true;
    private final String migrationCollectionName;
    /**
     * if true, Ox will throw an exception if the collections targeted by the OxActions or Migrations do not exist
     */
    private final boolean failOnMissingCollection;
    private final MongoClient mongo;
    private final String databaseName;

    public static MongoDBConnectorConfig fromOxConfig(OxConfig oxConfig) {
        return new MongoDBConnectorConfig(
                oxConfig.mongo(),
                oxConfig.databaseName(),
                oxConfig.collectionsConfig().migrationCollectionName(),
                oxConfig.collectionsConfig().createMigrationCollection(),
                oxConfig.extras().failOnMissingCollection()
        );
    }

    private MongoDBConnectorConfig(MongoClient mongo,
                                   String databaseName,
                                   String migrationCollectionName,
                                   boolean createMigrationCollectionIfNotExists,
                                   boolean failOnMissingCollection) {
        this.createMigrationCollection = createMigrationCollectionIfNotExists;
        this.mongo = mongo;
        this.databaseName = databaseName;
        this.migrationCollectionName = migrationCollectionName;
        this.failOnMissingCollection = failOnMissingCollection;
    }

    public boolean shouldCreateMigrationCollection() {
        return createMigrationCollection;
    }

    public String getMigrationCollectionName() {
        return migrationCollectionName;
    }

    public MongoClient getMongo() {
        return mongo;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * if true, Ox will throw an exception if the collections targeted by the OxActions or Migrations do not exist
     */
    public boolean shouldFailOnMissingCollection() {
        return failOnMissingCollection;
    }
}
