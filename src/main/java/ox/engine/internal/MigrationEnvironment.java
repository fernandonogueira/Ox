package ox.engine.internal;

import ox.utils.Log;
import com.mongodb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fernando Nogueira
 * @since 4/14/14 3:10 PM
 */
public class MigrationEnvironment {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationEnvironment.class);

    /**
     * If true, nothing will be executed. Just simulated (and printed).
     */
    private boolean simulate = false;
    private MongoDBConnector mongoConnector;

    public void execute(MigrateAction migrateAction) {

        long cmdStartTime = System.currentTimeMillis();

        LOG.info(Log.preff(" ----------- Executing action: " + migrateAction));

        if (isSimulate()) {
            LOG.info(Log.preff("[Simulate] Executing action: "));
            LOG.info(Log.preff(migrateAction.toString()));
        } else {
            mongoConnector.executeCommand(migrateAction);
        }

        long cmdEndTime = System.currentTimeMillis();

        LOG.info(Log.preff(" ----------- Action Executed. (" + (cmdEndTime - cmdStartTime) + "ms)"));
    }

    /**
     * @deprecated use {@link MigrationEnvironment#getMongoDatabase()} instead
     */
    @Deprecated
    public DB getMongoDatabae() {
        return getMongoDatabase();
    }

    /**
     * Retrieves the connected database
     *
     * @return the connected database
     */
    public DB getMongoDatabase() {
        return mongoConnector.getMongoDatabase();
    }

    public void setSimulate(boolean simulate) {
        this.simulate = simulate;
    }

    public boolean isSimulate() {
        return simulate;
    }

    public void setMongoConnector(MongoDBConnector mongoConnector) {
        this.mongoConnector = mongoConnector;
    }
}