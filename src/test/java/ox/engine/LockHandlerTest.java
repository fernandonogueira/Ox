package ox.engine;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import ox.engine.internal.Lock;
import ox.engine.internal.LockHandler;
import ox.engine.internal.MongoDBConnector;
import ox.engine.internal.MongoDBConnectorConfig;
import ox.integration.base.OxBaseContainerTest;
import ox.utils.Faker;

public class LockHandlerTest extends OxBaseContainerTest {

    @Test
    public void concurrentLockTest() {

        OxConfig oxConfig = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName(Faker.fakeDBName())
                .scanPackage("ox.db.migrations")
                .build();

        MongoDBConnector mongoDBConnector = new MongoDBConnector(MongoDBConnectorConfig.fromOxConfig(oxConfig));

        // given a lock handler
        LockHandler lockHandler = new LockHandler(oxConfig);

        // given an existing lock
        lockHandler.ensureLockCollectionExists();

        Lock lock = lockHandler.acquireLock();
        Assertions.assertThat(lock).isNotNull();

        // when trying to acquire the lock again
        Lock lock2 = lockHandler.acquireLock();

        Assertions.assertThat(lock2).isNull();
    }

}
