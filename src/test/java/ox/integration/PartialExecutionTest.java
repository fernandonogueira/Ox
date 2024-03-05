package ox.integration;

import com.mongodb.MongoClient;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.integration.base.OxBaseContainerTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PartialExecutionTest extends OxBaseContainerTest {

    @Test
    public void shouldExecuteMigrationToVersion2() throws InvalidMongoConfiguration {

        MongoClient mongo = getDefaultMongo();

        OxConfig config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("partialExecutionTest")
                .scanPackage("ox.db.migrations")
                .build();

        Ox ox = Ox.configure(config);

        ox.up(2);
        assertThat(ox.databaseVersion()).isEqualTo(2);
    }

}
