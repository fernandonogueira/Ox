package ox.ordered;

import com.mongodb.MongoClient;
import org.junit.Test;
import ox.OxBaseContainerTest;
import ox.engine.Ox;
import ox.engine.exception.InvalidMongoConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderedTest extends OxBaseContainerTest {

    @Test
    public void orderedMigrationsTest() throws InvalidMongoConfiguration {
        MongoClient mongo = getDefaultMongo();
        Ox ox = Ox.setUp(
                mongo,
                "ox.ordered.migrations.step1",
                "oxOrderedTest",
                true);
        Integer databaseVersion = ox.databaseVersion();
        assertThat(databaseVersion)
                .withFailMessage("Database is initializing. It should have version=0")
                .isEqualTo(0);
        ox.up();
        databaseVersion = ox.databaseVersion();
        assertThat(databaseVersion).isEqualTo(1);

        ox = Ox.setUp(
                mongo,
                "ox.ordered.migrations.step2",
                "oxOrderedTest",
                true);

        ox.up();
        databaseVersion = ox.databaseVersion();

        assertThat(databaseVersion).isEqualTo(3);
    }

}
