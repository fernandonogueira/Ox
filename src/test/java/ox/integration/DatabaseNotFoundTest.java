package ox.integration;

import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.engine.exception.DatabaseNotFoundException;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.integration.base.OxBaseContainerTest;
import ox.utils.Faker;

public class DatabaseNotFoundTest extends OxBaseContainerTest {

    @Test(expected = DatabaseNotFoundException.class)
    public void databaseNotFoundTest() throws InvalidMongoConfiguration {
        OxConfig config = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName(Faker.fakeDBName())
                .scanPackage("ox.db.migrations")
                .disableMigrationCollectionCreation()
                .build();

        Ox.setUp(config).up();
    }
}
