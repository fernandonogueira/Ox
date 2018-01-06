package ox.engine;

import ox.engine.exception.GenericMongoMigratorException;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.engine.exception.InvalidPackageToScan;
import ox.engine.exception.NoMigrationFileFoundException;
import ox.engine.internal.MigrationEnvironment;
import ox.engine.internal.MongoDBConnector;
import ox.engine.internal.MongoDBConnectorConfig;
import ox.engine.internal.resources.Location;
import ox.engine.internal.resources.ResolvedMigration;
import ox.engine.internal.resources.scanner.Scanner;
import ox.engine.structure.Migration;
import ox.utils.CollectionUtils;
import ox.utils.Log;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is basically the main class.
 * Here you can execute up() and down()
 * <p/>
 * If you want to simulate an execution, call .simulate()
 * <p/>
 * Usage:
 * <pre>
 *  MigratorEngine
 *  .setUp(mongoInstance, "ox.db.migrates", "databaseName", true)
 *  .up();
 * </pre>
 *
 * @author Fernando Nogueira
 * @since 4/11/14 3:01 PM
 */
public final class MigratorEngine {

    private static final Logger LOG = LoggerFactory.getLogger(MigratorEngine.class);

    private boolean simulate = false;
    private Mongo mongo;
    private String scanPackage;
    private MongoDBConnector mongoConnector;

    private MigratorEngine(Mongo mongo,
                           String scanPackage,
                           String databaseName,
                           boolean createVersioningCollectionIfDoesntExists) {

        this.mongo = mongo;
        this.scanPackage = scanPackage;
        this.mongoConnector =
                new MongoDBConnector(MongoDBConnectorConfig
                        .create()
                        .setMongoClient(mongo)
                        .setDatabaseName(databaseName)
                        .createCollectionIfDontExists(createVersioningCollectionIfDoesntExists));

    }

    public static MigratorEngine setUp(
            Mongo mongo,
            String scanPackage,
            String databaseName,
            boolean createVersioningCollectionIfDoesntExists) {

        return new MigratorEngine(mongo, scanPackage, databaseName, createVersioningCollectionIfDoesntExists);
    }

    /**
     * Run all migrations (UP)
     *
     * @throws InvalidMongoConfiguration
     */
    public void up() throws InvalidMongoConfiguration {
        validateExecution();
        List<ResolvedMigration> migrations = getMigrationsList();
        executeEachMigrate(migrations, ExecutionMode.UP, null);
    }

    public void up(int version) throws InvalidMongoConfiguration {
        validateExecution();
        List<ResolvedMigration> migrations = getMigrationsList();
        executeEachMigrate(migrations, ExecutionMode.UP, version);
    }

    /**
     * Run all migrations (DOWN)
     *
     * @throws InvalidMongoConfiguration
     */
    public void down() throws InvalidMongoConfiguration {
        validateExecution();
        List<ResolvedMigration> migrations = getMigrationsList();
        executeEachMigrate(migrations, ExecutionMode.DOWN, null);
    }

    public void down(int version) throws InvalidMongoConfiguration {
        validateExecution();
        List<ResolvedMigration> migrations = getMigrationsList();
        executeEachMigrate(migrations, ExecutionMode.DOWN, version);
    }

    private void validateExecution() throws InvalidMongoConfiguration {
        if (!simulate && mongo == null) {
            throw new InvalidMongoConfiguration("Invalid Mongo Configuration. Please fix it and try again.");
        }
    }

    private List<ResolvedMigration> resolveMigrations(String scanPackage)
            throws Exception {

        Scanner scanner = new Scanner(Thread.currentThread().getContextClassLoader());
        Class<?>[] resources = scanner.scanForClasses(new Location(scanPackage), Migration.class);

        List<ResolvedMigration> resolvedMigrations = new ArrayList<>();
        if (resources != null && resources.length > 0) {
            for (Class<?> resource : resources) {
                if (Migration.class.isAssignableFrom(resource)) {

                    Pattern pattern = Pattern.compile("V\\d*_");

                    Matcher matcher = pattern.matcher(resource.getCanonicalName());
                    if (matcher.find()) {
                        String string = matcher.group();

                        ResolvedMigration resolvedMigration = new ResolvedMigration();
                        Migration migration = ((Class<Migration>) resource).newInstance();
                        resolvedMigration.setMigrate(migration);

                        Integer version = Integer.valueOf(string.substring(1, string.length() - 1));
                        resolvedMigration.setVersion(version);

                        resolvedMigrations.add(resolvedMigration);

                        LOG.info(Log.preff("Resolved Migrate Found: " + resolvedMigration));
                    }
                }
            }
        }

        return resolvedMigrations;
    }

    private void executeEachMigrate(List<ResolvedMigration> migrations,
                                    ExecutionMode mode,
                                    Integer desiredVersion) {
        Integer currentVersion;
        if (!simulate) {
            currentVersion = mongoConnector.retrieveDatabaseCurrentVersion();
        } else {
            currentVersion = 0;
        }

        try {
            MigrationEnvironment env = new MigrationEnvironment();
            env.setSimulate(simulate);
            env.setMongoConnector(mongoConnector);

            LOG.info(Log.preff("MongoDB Database Current Version: " + currentVersion));

            List<ResolvedMigration> migrationsToProcess;
            if (ExecutionMode.DOWN.equals(mode)) {
                migrationsToProcess = new ArrayList<>(migrations);
                Collections.reverse(migrationsToProcess);
            } else {
                migrationsToProcess = migrations;
            }

            for (ResolvedMigration migration : migrationsToProcess) {

                long migrateStartTime = System.currentTimeMillis();
                boolean isMigrateVersionApplied = mongoConnector.verifyIfMigrateWasAlreadyExecuted(migration.getVersion());

                if (ExecutionMode.UP.equals(mode)) {
                    runMigrationUpIfApplies(desiredVersion, env, migration, migrateStartTime, isMigrateVersionApplied);
                } else {
                    runMigrationDownIfApplies(desiredVersion, env, migration, migrateStartTime, isMigrateVersionApplied);
                }
            }
        } catch (GenericMongoMigratorException e) {
            LOG.error(Log.preff("MongoDB Migrations Generic Error"), e);
        }

        LOG.info(Log.preff("Migration Finished!"));
    }

    private void runMigrationDownIfApplies(Integer desiredVersion, MigrationEnvironment env, ResolvedMigration migration, long migrateStartTime, boolean isMigrateVersionApplied) throws GenericMongoMigratorException {
        if (desiredVersion == null || migration.getVersion() > desiredVersion) {
            if (isMigrateVersionApplied) {
                LOG.info(Log.preff(" ------- Executing migrate (DOWN) Version: " + migration.getVersion() + " migration: " + migration));
                migration.getMigrate().down(env);
                mongoConnector.removeMigrationVersion(migration.getVersion());
                LOG.info(Log.preff(" ------- Migration Executed. (DOWN) Version: " + migration.getVersion() + " (" + (System.currentTimeMillis() - migrateStartTime) + "ms)"));
            } else {
                LOG.debug(Log.preff("(DOWN) Skipping migration. Migrate not applied. V" + migration.getVersion()));
            }
        } else {
            LOG.debug(Log.preff("Ignoring Migrate Version (DOWN) " + migration.getVersion() + ". Desired Version: " + desiredVersion));
        }
    }

    private void runMigrationUpIfApplies(Integer desiredVersion, MigrationEnvironment env, ResolvedMigration migration, long migrateStartTime, boolean isMigrateVersionApplied) throws GenericMongoMigratorException {
        if (desiredVersion == null || migration.getVersion() <= desiredVersion) {
            if (!isMigrateVersionApplied) {
                LOG.info(Log.preff(" ------- Executing migrate (UP) Version: " + migration.getVersion() + " migration: " + migration));
                migration.getMigrate().up(env);
                mongoConnector.insertMigrationVersion(migration.getVersion());
                LOG.info(Log.preff(" ------- Migration Executed. Version: " + migration.getVersion() + " (" + (System.currentTimeMillis() - migrateStartTime) + "ms)"));
            } else {
                LOG.debug(Log.preff("(UP) Skipping migration. Migrate already applied. V" + migration.getVersion()));
            }
        } else {
            LOG.debug(Log.preff("Ignoring Migrate Version (UP) " + migration.getVersion() + ". Desired Version: " + desiredVersion));
        }
    }

    public List<ResolvedMigration> getMigrationsList() {
        try {
            List<ResolvedMigration> resolvedMigrations = resolveMigrations(scanPackage);
            return CollectionUtils.sortResolvedMigrations(resolvedMigrations);
        } catch (IOException e) {
            LOG.error(Log.preff("Error updating MONGODB Database Schema"), e);
        } catch (NoMigrationFileFoundException e) {
            LOG.error(Log.preff("No Migration File Found Exception"), e);
        } catch (InvalidPackageToScan invalidPackageToScan) {
            LOG.error(Log.preff("Invalid package to scan."), invalidPackageToScan);
        } catch (ClassNotFoundException e) {
            LOG.error(Log.preff("Class not found error"), e);
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error(Log.preff("There was a problem instantiating a migrate class"), e);
        } catch (Exception e) {
            LOG.error(Log.preff("There was a problem instantiating a migrate class"), e);
        }
        return null;
    }

    public MigratorEngine simulate() {
        simulate = true;
        return this;
    }

    private enum ExecutionMode {
        UP, DOWN
    }

}