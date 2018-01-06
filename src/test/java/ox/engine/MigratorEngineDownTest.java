package ox.engine;

import com.mongodb.*;
import org.junit.Test;
import org.mockito.Mockito;
import ox.Configuration;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.engine.internal.MongoDBConnector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fernando Nogueira
 * @since 4/22/14 6:25 PM
 */
public class MigratorEngineDownTest {

    @Test
    public void runDownMigrationOnANonEmptyDBTest() throws InvalidMongoConfiguration {

        Mongo mongo = Mockito.mock(Mongo.class);
        MongoDBConnector mongoConnector = Mockito.mock(MongoDBConnector.class);
        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        DBCursor cursor = Mockito.mock(DBCursor.class);

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, 10);

        List<String> dbList = new ArrayList<>();
        dbList.add("string");

        Mockito.when(mongoConnector.retrieveDatabaseCurrentVersion()).thenReturn(10);
        Mockito.when(mongo.getDatabaseNames()).thenReturn(dbList);
        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.collectionExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.find()).thenReturn(cursor);
        Mockito.when(cursor.sort(Mockito.any(DBObject.class))).thenReturn(cursor);
        Mockito.when(cursor.limit(Mockito.anyInt())).thenReturn(cursor);

        Mockito.when(cursor.hasNext()).thenReturn(true).thenReturn(false);
        Mockito.when(cursor.next()).thenReturn(dbObject);

        MigratorEngine engine = MigratorEngine
                .setUp(mongo, "ox.db.migrates", "string", true);


        engine.down();
    }
}
