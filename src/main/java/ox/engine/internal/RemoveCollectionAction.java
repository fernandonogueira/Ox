package ox.engine.internal;

import ox.engine.exception.InvalidMigrateActionException;
import com.mongodb.Mongo;

/**
 * Created by matheus on 16/02/16.
 */
public class RemoveCollectionAction extends OxAction {
    public RemoveCollectionAction(String collection) {
        this.collection = collection;
    }

    @Override
    public String getCollection() {
        return collection;
    }

    @Override
    protected void validateAction() throws InvalidMigrateActionException {
        if (collection == null || collection.isEmpty())
            throw new InvalidMigrateActionException("Invalid Migrate action.");
    }

    @Override
    void runAction(MongoDBConnector mongoDBConnector, Mongo mongo, String databaseName) {
        if (mongoDBConnector.verifyIfCollectionExists(collection)) {
            mongoDBConnector.removeCollection(collection);
        }
    }
}
