package ox.engine;

import ox.engine.exception.InvalidMongoConfiguration;
import ox.engine.internal.MongoDBConnector;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class OxMigrationTest {

    @Mock
    private MongoDBConnector mongoConnector;

    @Mock
    private Mongo mongo;

    @Mock
    private DB db;

    @Mock
    private DBCollection coll;

    @Test
    public void runMigrationsTest() throws IOException, InvalidMongoConfiguration {

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);

        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(coll);

        Ox engine = Ox.setUp(mongo, "ox.db.migrates", "myDB", true);
        engine.simulate()
                .up();
    }

    @Test
    public void runDownMigrationTest() throws InvalidMongoConfiguration {

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(coll);
        Mockito.when(coll.count(Mockito.any(DBObject.class))).thenReturn(1l);

        Ox
                .setUp(mongo, "ox.db.migrates", "myDB", true)
                .simulate()
                .down();
    }

    @Test(expected = InvalidMongoConfiguration.class)
    public void validateInvalidMongoInstance() throws InvalidMongoConfiguration {

        Ox
                .setUp(null, "ox.db.migrates", "myDB", true)
                .up();
    }

}
