package ox.engine.internal;

import ox.engine.exception.InvalidMigrateActionException;
import com.mongodb.Mongo;

/**
 * @author Fernando Nogueira
 * @since 4/11/14 3:04 PM
 */
public abstract class MigrateAction {

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
     * @param env The MigrationEnvironment instance
     * @throws InvalidMigrateActionException
     */
    public void execute(MigrationEnvironment env) throws InvalidMigrateActionException {
        validateAction();
        env.execute(this);
    }

    abstract void runAction(MongoDBConnector mongoDBConnector, Mongo mongo, String databaseName);
}