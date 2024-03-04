package ox.engine;

import com.mongodb.MongoClient;

public record OxConfig(
        MongoClient mongo,
        String databaseName,
        String scanPackage,
        boolean createMigrationCollection,
        String migrationCollectionName,
        boolean failOnMissingCollection,
        boolean dryRun
) {
    public static OxConfigBuilder builder() {
        return new OxConfigBuilder();
    }
}

