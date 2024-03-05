package ox.engine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.engine.exception.InvalidMongoDatabaseConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class OxMigrationTest {

    @Test(expected = InvalidMongoDatabaseConfiguration.class)
    public void validateInvalidMongoInstance() throws InvalidMongoConfiguration {
        Ox.configure(null, "ox.db.migrations", "myDB").up();
    }

}
