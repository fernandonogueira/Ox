//package ox.engine;
//
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.Mongo;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.runners.MockitoJUnitRunner;
//import ox.engine.exception.InvalidMongoConfiguration;
//import ox.engine.exception.InvalidPackageToScanException;
//import ox.engine.internal.MongoDBConnector;
//
//@RunWith(MockitoJUnitRunner.class)
//public class OxInvalidPackageTest {
//
//    @Mock
//    private MongoDBConnector mongoConnector;
//
//    @Mock
//    private Mongo mongo;
//
//    @Mock
//    private DB db;
//
//    @Mock
//    private DBCollection coll;
//
//    @Test(expected = InvalidPackageToScanException.class)
//    public void invalidPackageTest() throws InvalidMongoConfiguration {
//
//        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
//
//        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(coll);
//
//        Ox engine = Ox.setUp(mongo, "ox.ox", "myDB", true);
//        engine.simulate()
//                .up();
//    }
//
//}
