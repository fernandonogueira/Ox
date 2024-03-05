package ox.integration.sequenced;

import com.mongodb.MongoClient;
import org.junit.Test;
import ox.engine.OxConfig;
import ox.integration.base.OxBaseContainerTest;
import ox.engine.Ox;
import ox.engine.exception.InvalidMongoConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class SequencedTest extends OxBaseContainerTest {

    @Test
    public void orderedMigrationsTest() throws InvalidMongoConfiguration {
        MongoClient mongo = getDefaultMongo();

        OxConfig config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("oxSequencedTest")
                .scanPackage("ox.integration.sequenced.migrations.step1")
                .build();

        Ox ox = Ox.configure(config);

        Integer databaseVersion = ox.databaseVersion();
        assertThat(databaseVersion)
                .withFailMessage("Database is initializing. It should have version=0")
                .isEqualTo(0);

        ox.up();
        databaseVersion = ox.databaseVersion();
        assertThat(databaseVersion).isEqualTo(1);

        config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("oxSequencedTest")
                .scanPackage("ox.integration.sequenced.migrations.step2")
                .build();

        ox = Ox.configure(config);

        ox.up();
        databaseVersion = ox.databaseVersion();

        assertThat(databaseVersion).isEqualTo(3);
    }

}
