package ox.engine;

import com.mongodb.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.engine.internal.MongoDBConnector;

@RunWith(MockitoJUnitRunner.class)
public class OxMigrationTest {

    @Mock
    private MongoDBConnector mongoConnector;

    @Mock
    private MongoClient mongo;

    @Mock
    private DB db;

    @Mock
    private DBCollection coll;

    @Test
    public void runMigrationsTest() throws InvalidMongoConfiguration {

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);

        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(coll);

        Ox engine = Ox.setUp(mongo, "ox.db.migrations", "myDB", true);
        engine.simulate()
                .up();
    }

    @Test
    public void runDownMigrationTest() throws InvalidMongoConfiguration {

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(coll);
        Mockito.when(coll.count(Mockito.any(DBObject.class))).thenReturn(1L);

        Ox
                .setUp(mongo, "ox.db.migrations", "myDB", true)
                .simulate()
                .down();
    }

    @Test(expected = InvalidMongoConfiguration.class)
    public void validateInvalidMongoInstance() throws InvalidMongoConfiguration {

        Ox.setUp(
                null,
                "ox.db.migrations",
                "myDB")
                .up();
    }

}
