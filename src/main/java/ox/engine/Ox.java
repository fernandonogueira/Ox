package ox.engine;

import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import ox.engine.exception.InvalidMongoClientConfiguration;
import ox.engine.exception.InvalidMongoDatabaseConfiguration;
import ox.engine.exception.InvalidReadPreferenceException;
import ox.engine.internal.*;
import ox.utils.CollectionUtils;
import ox.utils.logging.Logger;
import ox.utils.logging.Loggers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Usage:
 * <pre>
 *  Ox
 *  .configure(mongoInstance, "ox.db.migrates", "databaseName")
 *  .up();
 * </pre>
 */
public final class Ox {

    private static final Logger LOG = Loggers.getLogger(Ox.class);
    private final OxConfig config;
    private final MongoDBConnector mongoConnector;
    private final LockHandler lockHandler;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MigrationResolver migrationResolver = new MigrationResolver();

    private Ox(OxConfig config, LockHandler lockHandler) {
        this.config = config;
        this.mongoConnector = new MongoDBConnector(
                MongoDBConnectorConfig.fromOxConfig(config)
        );
        this.lockHandler = lockHandler;
    }

    private static void validateConfig(OxConfig config) {
        if (config.scanPackage() == null || config.scanPackage().isEmpty()) {
            throw new IllegalArgumentException("Invalid scanPackage.");
        }
        if (config.databaseName() == null || config.databaseName().isEmpty()) {
            throw new InvalidMongoDatabaseConfiguration("Invalid databaseName");
        }
        if (config.mongo() == null) {
            throw new InvalidMongoClientConfiguration("MongoClient is null. Please provide a valid MongoClient.");
        }
        if (ReadPreference.primary() != config.mongo().getReadPreference()) {
            throw new InvalidReadPreferenceException();
        }
        if (config.collectionsConfig() == null) {
            throw new IllegalArgumentException("Invalid collectionsConfig.");
        }
        if (config.collectionsConfig().migrationCollectionName() == null || config.collectionsConfig().migrationCollectionName().isEmpty()) {
            throw new IllegalArgumentException("Invalid migrationCollectionName.");
        }
        if (config.collectionsConfig().lockCollectionName() == null || config.collectionsConfig().lockCollectionName().isEmpty()) {
            throw new IllegalArgumentException("Invalid lockCollectionName.");
        }
    }

    public static Ox configure(
            OxConfig config
    ) {
        validateConfig(config);
        LockHandler lockHandler = new LockHandler(config);
        lockHandler.ensureLockCollectionExists();
        return new Ox(config, lockHandler);
    }

    public static Ox configure(
            MongoClient mongo,
            String scanPackage,
            String databaseName) {

        OxConfig oxConfig = OxConfig.builder()
                .mongo(mongo)
                .scanPackage(scanPackage)
                .databaseName(databaseName)
                .build();

        return configure(oxConfig);
    }

    /**
     * Run all migrations (UP)
     */
    public void up() {
        List<ResolvedMigration> migrations = getMigrationsList();
        lockAndExecute(migrations, ExecutionMode.UP, null);
    }

    public void up(int version) {
        List<ResolvedMigration> migrations = getMigrationsList();
        lockAndExecute(migrations, ExecutionMode.UP, version);
    }

    /**
     * Run all migrations (DOWN)
     */
    public void down() {
        List<ResolvedMigration> migrations = getMigrationsList();
        lockAndExecute(migrations, ExecutionMode.DOWN, null);
    }

    public void down(int version) {
        List<ResolvedMigration> migrations = getMigrationsList();
        lockAndExecute(migrations, ExecutionMode.DOWN, version);
    }

    private void lockAndExecute(
            List<ResolvedMigration> migrations,
            ExecutionMode mode,
            Integer desiredVersion
    ) {
        Lock lock = null;
        try {
            do {
                lock = lockHandler.acquireLock();
                LOG.info("[Ox] Waiting for lock...");
                Thread.sleep(1000);
            } while (lock == null);

            LockRefresher lockRefresher = new LockRefresher(lockHandler, lock, 1000);
            Future<?> refresherFuture = executor.submit(lockRefresher);

            LOG.debug("[Ox] Lock acquired. Running migrations...");
            MigrationRunner migrationRunner = new MigrationRunner(mongoConnector, config);
            migrationRunner.setup(migrations, mode, desiredVersion);
            migrationRunner.run();
            lockRefresher.complete();
            refresherFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            lockHandler.releaseLock(lock);
        }
    }

    public List<ResolvedMigration> getMigrationsList() {
        try {
            List<ResolvedMigration> resolvedMigrations = migrationResolver.resolveMigrations(config.scanPackage());
            return CollectionUtils.sortResolvedMigrations(resolvedMigrations);
        } catch (IOException e) {
            LOG.error("[Ox] Error updating MONGODB Database Schema", e);
        } catch (ClassNotFoundException e) {
            LOG.error("[Ox] Class not found error", e);
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("[Ox] There was a problem instantiating a migrate class", e);
        } catch (Exception e) {
            LOG.error("[Ox] There was a problem (generic) instantiating a migrate class", e);
        }
        return new ArrayList<>();
    }

    public Integer databaseVersion() {
        return mongoConnector.retrieveDatabaseCurrentVersion();
    }

}
