package ox.engine;

import com.mongodb.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.Configuration;
import ox.engine.exception.InvalidMongoConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class OxMigrationDownTest {

    @Mock
    private MongoClient mongo;

    @Test
    public void runDownMigrationOnANonEmptyDBTest() throws InvalidMongoConfiguration {

        DB db = Mockito.mock(DB.class);
        DBCollection collection = Mockito.mock(DBCollection.class);

        DBCursor cursor = Mockito.mock(DBCursor.class);

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append(Configuration.MIGRATION_COLLECTION_VERSION_ATTRIBUTE, 10);

        List<String> dbList = new ArrayList<>();
        dbList.add("string");

        Mockito.when(mongo.getDatabaseNames()).thenReturn(dbList);
        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.collectionExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);
        Mockito.when(collection.find()).thenReturn(cursor);
        Mockito.when(cursor.sort(Mockito.any(DBObject.class))).thenReturn(cursor);
        Mockito.when(cursor.limit(Mockito.anyInt())).thenReturn(cursor);

        Mockito.when(cursor.hasNext()).thenReturn(true).thenReturn(false);
        Mockito.when(cursor.next()).thenReturn(dbObject);

        Ox engine = Ox.setUp(
                mongo,
                "ox.db.migrations",
                "string");


        engine.down();

        assertThat(engine.databaseVersion()).isEqualTo(0);
    }
}
