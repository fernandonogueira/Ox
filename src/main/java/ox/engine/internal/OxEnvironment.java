package ox.engine.internal;

import com.mongodb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OxEnvironment {

    private static final Logger LOG = LoggerFactory.getLogger(OxEnvironment.class);

    /**
     * If true, nothing will be executed. Just simulated (and printed).
     */
    private boolean simulate = false;
    private MongoDBConnector mongoConnector;

    public void execute(OxAction oxAction) {

        long cmdStartTime = System.currentTimeMillis();

        LOG.info("[Ox] ----------- Executing action: {}", oxAction);

        if (isSimulate()) {
            LOG.info("[Ox] [Simulate] Executing action: ");
            LOG.info(oxAction.toString());
        } else {
            mongoConnector.executeCommand(oxAction);
        }

        long cmdEndTime = System.currentTimeMillis();

        LOG.info("[Ox] ----------- Action Executed. ({}ms)", (cmdEndTime - cmdStartTime));
    }

    /**
     * @deprecated use {@link OxEnvironment#getMongoDatabase()} instead
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