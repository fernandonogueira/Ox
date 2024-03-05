package ox.integration;

import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxCollectionsConfig;
import ox.engine.OxConfig;
import ox.engine.exception.MissingMigrationHistoryCollectionException;
import ox.integration.base.OxBaseContainerTest;
import ox.utils.Faker;

public class DatabaseNotFoundTest extends OxBaseContainerTest {

    @Test(expected = MissingMigrationHistoryCollectionException.class)
    public void databaseNotFoundTest() {
        OxConfig config = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName(Faker.fakeDBName())
                .scanPackage("ox.db.migrations")
                .collectionsConfig(OxCollectionsConfig.builder()
                        .createMigrationCollection(false)
                        .build())
                .build();

        Ox.configure(config).up();
    }
}
