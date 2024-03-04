package ox.engine.internal;

import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ox.engine.exception.InvalidMigrateActionException;

public class RemoveIndexAction extends OxAction {

    private static final Logger LOG = LoggerFactory.getLogger(RemoveIndexAction.class);
    private final String indexName;

    public RemoveIndexAction(String indexName) {
        this.indexName = indexName;
    }

    /**
     * Set the collection that the index will be removed.
     *
     * @param collection the collection that the index should be removed
     */
    public RemoveIndexAction setCollection(String collection) {
        this.collection = collection;
        return this;
    }

    @Override
    public String getCollection() {
        return collection;
    }

    @Override
    protected void validateAction() throws InvalidMigrateActionException {
        if (indexName == null || indexName.equals("")) {
            throw new InvalidMigrateActionException("Index Name not set. Index Remove Action cannot be executed.");
        }
        if (collection == null) {
            throw new InvalidMigrateActionException("Collection not set. Index Remove Action cannot be executed");
        }
    }

    @Override
    void runAction(MongoDBConnector mongoDBConnector, MongoClient mongo, String databaseName) {

        boolean doesItExists = mongoDBConnector.verifyIfIndexExists(null, indexName, collection);

        if (doesItExists) {
            LOG.info("[Ox] Index exists! Removing... Index name: " + indexName);
            mongo.getDB(databaseName).getCollection(collection).dropIndex(indexName);
        } else {
            LOG.warn("[Ox] Ignoring Index Removal Action " +
                    "because no index was found with name: " + indexName);
        }

    }
}
