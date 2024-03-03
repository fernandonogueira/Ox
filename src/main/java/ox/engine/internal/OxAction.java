package ox.engine.internal;

import com.mongodb.MongoClient;
import ox.engine.exception.InvalidMigrateActionException;

public abstract class OxAction {

    protected String collection;

    /**
     * Create a new CreateIndex action.
     *
     * @param indexName the index name
     * @return a CreateIndexAction
     */
    public static CreateIndexAction createIndex(String indexName) {
        return new CreateIndexAction(indexName);
    }

    /**
     * Create a new RemoveIndexAction
     *
     * @param indexName the index name
     * @return a RemoveIndexAction
     */
    public static RemoveIndexAction removeIndex(String indexName) {
        return new RemoveIndexAction(indexName);
    }

    public static RemoveCollectionAction removeCollection(String collectionName) {
        return new RemoveCollectionAction(collectionName);
    }

    public abstract String getCollection();

    protected abstract void validateAction() throws InvalidMigrateActionException;

    /**
     * Executes this migrate action
     *
     * @param env The OxEnvironment instance
     * @throws InvalidMigrateActionException if migration is not valid
     */
    public void execute(OxEnvironment env) throws InvalidMigrateActionException {
        validateAction();
        env.execute(this);
    }

    abstract void runAction(MongoDBConnector mongoDBConnector, MongoClient mongo, String databaseName);
}
