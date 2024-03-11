package ox.integration.configs;

import com.mongodb.MongoClient;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxCollectionsConfig;
import ox.engine.OxConfig;
import ox.engine.exception.InvalidMongoDatabaseConfiguration;
import ox.engine.exception.InvalidScanPackageException;

public class ValidateConfigsTest {

    @Test(expected = InvalidScanPackageException.class)
    public void shouldThrowInvalidScanPackageException() {
        OxConfig config = OxConfig.builder()
                .scanPackage(null)
                .build();

        Ox.configure(config);
    }

    @Test(expected = InvalidMongoDatabaseConfiguration.class)
    public void shouldThrowInvalidMongoDatabaseException() {
        OxConfig config = OxConfig.builder()
                .scanPackage("package")
                .databaseName(null)
                .build();

        Ox.configure(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateInvalidCollectionsConfig() {

        OxConfig config = OxConfig.builder()
                .mongo(new MongoClient())
                .scanPackage("package")
                .databaseName("random_db")
                .collectionsConfig(null)
                .build();

        Ox.configure(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateInvalidMigrationsCollection() {
        OxConfig config = OxConfig.builder()
                .scanPackage("package")
                .databaseName("random_db")
                .mongo(new MongoClient())
                .collectionsConfig(OxCollectionsConfig.builder()
                        .migrationCollectionName(null)
                        .build())
                .build();

        Ox.configure(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateInvalidLockCollection() {
        OxConfig config = OxConfig.builder()
                .scanPackage("package")
                .databaseName("random_db")
                .mongo(new MongoClient())
                .collectionsConfig(OxCollectionsConfig.builder()
                        .migrationCollectionName("migrations")
                        .lockCollectionName("")
                        .build())
                .build();

        Ox.configure(config);
    }

}
