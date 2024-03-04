package ox.engine;

import com.mongodb.MongoClient;
import ox.Configuration;

public class OxConfigBuilder {
    private boolean createMigrationCollection = true;
    private String migrationCollectionName = Configuration.SCHEMA_VERSION_COLLECTION_NAME;
    private boolean failOnMissingCollection = false;
    private MongoClient mongo;
    private String databaseName;
    private String scanPackage;
    private boolean dryRun = false;

    /**
     * By default, Ox will create the migration collection if it does not exists.
     * <p/>
     * If you want to disable this behavior, call this method
     */
    public OxConfigBuilder disableMigrationCollectionCreation() {
        this.createMigrationCollection = false;
        return this;
    }

    public OxConfigBuilder migrationCollectionName(String migrationCollectionName) {
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
    public OxConfigBuilder failOnMissingCollection(boolean failOnMissingCollection) {
        this.failOnMissingCollection = failOnMissingCollection;
        return this;
    }

    public OxConfigBuilder mongo(MongoClient mongo) {
        this.mongo = mongo;
        return this;
    }

    public OxConfigBuilder databaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public OxConfigBuilder scanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
        return this;
    }

    /**
     * Enables dry run mode. Ox will only simulate the migration process, it will not execute the migrations
     */
    public OxConfigBuilder dryRun() {
        this.dryRun = true;
        return this;
    }

    public OxConfig build() {
        return new OxConfig(
                mongo,
                databaseName,
                scanPackage,
                createMigrationCollection,
                migrationCollectionName,
                failOnMissingCollection,
                dryRun
        );
    }
}
