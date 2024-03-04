package ox.engine.internal;

import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OxEnvironment {

    private static final Logger LOG = LoggerFactory.getLogger(OxEnvironment.class);

    /**
     * If true, nothing will be executed. Just simulated (and printed).
     */
    private boolean dryRun = false;
    private MongoDBConnector mongoConnector;

    public void execute(OxAction oxAction) {

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
