package ox.engine;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import ox.engine.internal.Lock;
import ox.engine.internal.LockHandler;
import ox.engine.internal.LockRefresher;
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

    @Test
    public void releaseLockTest() {

        OxConfig oxConfig = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName(Faker.fakeDBName())
                .scanPackage("ox.db.migrations")
                .build();

        // given a lock handler
        LockHandler lockHandler = new LockHandler(oxConfig);

        // given an existing lock
        lockHandler.ensureLockCollectionExists();

        Lock lock = lockHandler.acquireLock();
        Assertions.assertThat(lock).isNotNull();

        lockHandler.releaseLock(lock);

        // when trying to acquire the lock again
        Lock lock2 = lockHandler.acquireLock();

        Assertions.assertThat(lock2).isNotNull();
    }

    @Test
    public void failToRefreshLockTest() {

        OxConfig oxConfig = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName(Faker.fakeDBName())
                .scanPackage("ox.db.migrations")
                .build();

        // given a lock handler
        LockHandler lockHandler = new LockHandler(oxConfig);

        // given an existing lock
        lockHandler.ensureLockCollectionExists();
        Lock lock = lockHandler.acquireLock();
        Assertions.assertThat(lock).isNotNull();

        // given a lock refresher
        LockRefresher refresher = new LockRefresher(lockHandler, lock, 100);

        // given the lock is already released
        lockHandler.releaseLock(lock);

        // refresher should fail to refresh the lock
        refresher.run();
    }


}
