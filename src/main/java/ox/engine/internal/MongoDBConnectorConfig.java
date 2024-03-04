package ox.engine.internal;

import com.mongodb.MongoClient;
import ox.Configuration;
import ox.engine.OxConfig;

/**
 * MongoDBConnector Configuration.
 * <p/>
 * createCollectionIfDontExists is true by default
 */
public final class MongoDBConnectorConfig {

    private boolean createMigrationCollection = true;
    private String migrationCollectionName;
    /**
     * if true, Ox will throw an exception if the collections targeted by the OxActions or Migrations do not exist
     */
    private boolean failOnMissingCollection;
    private MongoClient mongo;
    private String databaseName;

    public static class Builder {
        private boolean createMigrationCollectionIfNotExists = true;
        private MongoClient mongo;
        private String databaseName;
        private String migrationCollectionName = Configuration.SCHEMA_VERSION_COLLECTION_NAME;
        private boolean failOnMissingCollection = false;

        private Builder() {
        }

        /**
         * Set to true if you want to create the migration collection if it does not exists
         * <p/>
         * Defaults to true
         */
        public Builder createMigrationCollection(boolean value) {
            this.createMigrationCollectionIfNotExists = value;
            return this;
        }

        public Builder setMongoClient(MongoClient mongo) {
            this.mongo = mongo;
            return this;
        }

        public Builder setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Builder setMigrationCollectionName(String migrationCollectionName) {
            this.migrationCollectionName = migrationCollectionName;
            return this;
        }

        /**
         * if true, Ox will throw an exception if the collections targeted by the OxActions or Migrations do not exist
         * <p/>
         * Defaults to false
         * <p/>
         * Attention: This is a global setting, it will affect all OxActions and Migrations.
         * If false, Ox will ignore the missing collection and continue the migration process.
         */
        public Builder setFailOnMissingCollection(boolean failOnMissingCollection) {
            this.failOnMissingCollection = failOnMissingCollection;
            return this;
        }

        public MongoDBConnectorConfig build() {
            return new MongoDBConnectorConfig(
                    this.mongo,
                    this.databaseName,
                    this.migrationCollectionName,
                    this.createMigrationCollectionIfNotExists,
                    this.failOnMissingCollection
            );
        }
    }

    public static MongoDBConnectorConfig.Builder builder() {
        return new Builder();
    }

    public static MongoDBConnectorConfig fromOxConfig(OxConfig oxConfig) {
        return new MongoDBConnectorConfig(
                oxConfig.mongo(),
                oxConfig.databaseName(),
                oxConfig.migrationCollectionName(),
                oxConfig.createMigrationCollection(),
                oxConfig.failOnMissingCollection()
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
