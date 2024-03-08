package ox.engine.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ox.engine.OxConfig;
import ox.engine.exception.OxException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MigrationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationRunner.class);

    private final MongoDBConnector mongoConnector;
    private final OxConfig config;
    private List<ResolvedMigration> migrations;
    private ExecutionMode mode;
    private Integer desiredVersion;

    public MigrationRunner(
            MongoDBConnector mongoConnector,
            OxConfig config) {
        this.mongoConnector = mongoConnector;
        this.config = config;
    }

    public void setup(List<ResolvedMigration> migrations,
                      ExecutionMode mode,
                      Integer desiredVersion) {
        this.migrations = migrations;
        this.mode = mode;
        this.desiredVersion = desiredVersion;
    }

    private void executeEachMigrate() {
        Integer currentVersion;
        if (!config.extras().dryRun()) {
            currentVersion = mongoConnector.retrieveDatabaseCurrentVersion();
        } else {
            currentVersion = 0;
        }

        try {
            OxEnvironmentImpl env = new OxEnvironmentImpl();
            env.dryRun(config.extras().dryRun());
            env.setMongoConnector(mongoConnector);

            LOG.info("[Ox] MongoDB Database Current Version: " + currentVersion);

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
        } catch (OxException e) {
            LOG.error("[Ox] Runtime error", e);
        }
        LOG.info("[Ox] Migration Finished!");
    }

    private void runMigrationUpIfApplies(Integer desiredVersion, OxEnvironment env, ResolvedMigration migration, long migrateStartTime, boolean isMigrateVersionApplied) throws OxException {
        if (desiredVersion == null || migration.getVersion() <= desiredVersion) {
            if (!isMigrateVersionApplied) {
                LOG.info("[Ox] ------- Executing migrate (UP) Version: " + migration.getVersion() + " migration: " + migration);
                migration.getMigrate().up(env);
                mongoConnector.insertMigrationVersion(migration.getVersion());
                LOG.info("[Ox] ------- Migration Executed. Version: " + migration.getVersion() + " (" + (System.currentTimeMillis() - migrateStartTime) + "ms)");
            } else {
                LOG.debug("[Ox](UP) Skipping migration. Migrate already applied. V" + migration.getVersion());
            }
        } else {
            LOG.debug("[Ox] Ignoring Migrate Version (UP) " + migration.getVersion() + ". Desired Version: " + desiredVersion);
        }
    }

    private void runMigrationDownIfApplies(Integer desiredVersion, OxEnvironment env, ResolvedMigration migration, long migrateStartTime, boolean isMigrateVersionApplied) throws OxException {
        if (desiredVersion == null || migration.getVersion() > desiredVersion) {
            if (isMigrateVersionApplied) {
                LOG.info("[Ox] ------- Executing migrate (DOWN) Version: " + migration.getVersion() + " migration: " + migration);
                migration.getMigrate().down(env);
                mongoConnector.removeMigrationVersion(migration.getVersion());
                LOG.info("[Ox] ------- Migration Executed. (DOWN) Version: " + migration.getVersion() + " (" + (System.currentTimeMillis() - migrateStartTime) + "ms)");
            } else {
                LOG.debug("[Ox] (DOWN) Skipping migration. Migrate not applied. V" + migration.getVersion());
            }
        } else {
            LOG.debug("[Ox] Ignoring Migrate Version (DOWN) " + migration.getVersion() + ". Desired Version: " + desiredVersion);
        }
    }

    public void run() {
        if (migrations == null || migrations.isEmpty() || mode == null) {
            LOG.warn("[Ox] No migrations to execute");
            return;
        }
        this.executeEachMigrate();
    }

}
