package ox.ordered;

import com.mongodb.MongoClient;
import org.junit.Test;
import ox.OxBaseContainerTest;
import ox.engine.Ox;
import ox.engine.exception.InvalidMongoConfiguration;

public class OrderedTest extends OxBaseContainerTest {

    @Test
    public void orderedMigrationsTest() throws InvalidMongoConfiguration {
        MongoClient mongo = getDefaultMongo();
        Ox ox = Ox.setUp(
                mongo,
                "ox.ordered.migrations.step1",
                "oxOrderedTest",
                true);
        ox.up();
    }

}
