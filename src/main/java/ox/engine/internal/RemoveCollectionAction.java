package ox.engine.internal;

import com.mongodb.MongoClient;
import ox.engine.exception.InvalidMigrateActionException;

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
    void runAction(MongoDBConnector mongoDBConnector, MongoClient mongo, String databaseName) {
        if (mongoDBConnector.collectionExists(collection)) {
            mongoDBConnector.removeCollection(collection);
        }
    }
}
