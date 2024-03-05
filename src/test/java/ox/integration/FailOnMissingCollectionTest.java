package ox.integration;

import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.engine.OxConfigExtras;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.engine.exception.MissingCollectionException;
import ox.integration.base.OxBaseContainerTest;

public class FailOnMissingCollectionTest extends OxBaseContainerTest {

    @Test(expected = MissingCollectionException.class)
    public void shouldFailOnMissingCollection() throws InvalidMongoConfiguration {
        OxConfig config = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName("fail_on_missing_collection_db")
                .scanPackage("ox.db.migrations")
                .extras(OxConfigExtras.builder().failOnMissingCollection(true).build())
                .build();

        Ox.configure(config).up();
    }

}
