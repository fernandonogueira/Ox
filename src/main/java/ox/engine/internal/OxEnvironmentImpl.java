package ox.engine.internal;

import com.mongodb.client.MongoDatabase;
import ox.engine.exception.InvalidMigrateActionException;
import ox.utils.logging.Logger;
import ox.utils.logging.Loggers;

public class OxEnvironmentImpl implements OxEnvironment {

    private static final Logger LOG = Loggers.getLogger(OxEnvironment.class);

    /**
     * If true, nothing will be executed. Just simulated (and printed).
     */
    private boolean dryRun = false;
    private MongoDBConnector mongoConnector;

    public void execute(OxAction oxAction) throws InvalidMigrateActionException {
        oxAction.validateAction();

        long cmdStartTime = System.currentTimeMillis();

        LOG.info("[Ox] ----------- Executing action: {}", oxAction);

        if (isDryRun()) {
            LOG.info("[Ox] [Simulate] Executing action: ");
            LOG.info(oxAction.toString());
        } else {
            mongoConnector.executeCommand(oxAction);
        }

        long cmdEndTime = System.currentTimeMillis();

        LOG.info("[Ox] ----------- Action Executed. ({}ms)", (cmdEndTime - cmdStartTime));
    }

    /**
     * Retrieves the connected database
     *
     * @return the connected database
     */
    public MongoDatabase getMongoDatabase() {
        return mongoConnector.getMongoDatabase();
    }

    public void dryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setMongoConnector(MongoDBConnector mongoConnector) {
        this.mongoConnector = mongoConnector;
    }
}
