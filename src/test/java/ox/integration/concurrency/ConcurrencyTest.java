package ox.integration.concurrency;

import com.mongodb.MongoClient;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.integration.base.OxBaseContainerTest;

public class ConcurrencyTest extends OxBaseContainerTest {

    private OxConfig getConfig() {
        MongoClient mongo = getDefaultMongo();
        return OxConfig.builder()
                .mongo(mongo)
                .databaseName("concurrency-test")
                .scanPackage("ox.integration.concurrency.migrations")
                .build();
    }

    @Test
    public void concurrentMigrationsTest() throws InterruptedException {

        OxConfig config1 = getConfig();
        Ox ox = Ox.configure(config1);

        OxConfig config2 = getConfig();
        Ox ox2 = Ox.configure(config2);

        Thread t1 = new Thread(ox::up);
        t1.start();

        Thread t2 = new Thread(ox2::up);
        t2.start();

        t1.join();
        t2.join();
    }
}
