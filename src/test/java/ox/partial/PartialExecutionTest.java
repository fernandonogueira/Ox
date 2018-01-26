package ox.partial;

import com.mongodb.MongoClient;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.test.base.OxBaseContainerTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PartialExecutionTest extends OxBaseContainerTest {

    @Test
    public void shouldExecuteMigrationToVersion2() throws InvalidMongoConfiguration {

        MongoClient mongo = getDefaultMongo();
        Ox ox = Ox.setUp(
                mongo,
                "ox.db.migrations",
                "partialExecutionTest",
                true);

        ox.up(2);
        assertThat(ox.databaseVersion()).isEqualTo(2);
    }

}
