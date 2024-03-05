package ox.engine.internal;

import com.mongodb.client.MongoDatabase;
import ox.engine.exception.InvalidMigrateActionException;

public interface OxEnvironment {

    void execute(OxAction oxAction) throws InvalidMigrateActionException;
    MongoDatabase getMongoDatabase();

}

