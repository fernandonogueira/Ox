package ox.engine.internal;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ox.engine.structure.OrderingType;

@RunWith(MockitoJUnitRunner.class)
public class ValidateCreateGeoSpatialIndexTest {

    @InjectMocks
    private CreateIndexAction action;

    @Mock
    private OxEnvironment environment;

    @Mock
    private Mongo mongo;

    @Mock
    private MongoDBConnector connector;

    @Test
    public void createIndexGeospatialType() {
        OxAction action = OxAction
                .createIndex("IndexName")
                .setCollection("myCollection")
                .addAttribute("myPositionAttr", OrderingType.GEO_2DSPHERE);
    }

    @Test
    public void generateGeospatialIndexCommand() {
        OxAction action = OxAction
                .createIndex("IndexName")
                .setCollection("myCollection")
                .addAttribute("myPositionAttr", OrderingType.GEO_2DSPHERE);

        action.toString();
    }

    @Test
    public void runActionToCreateGeospatialIndex() {
        CreateIndexAction action = OxAction
                .createIndex("IndexName")
                .setCollection("myCollection")
                .addAttribute("myPositionAttr", OrderingType.GEO_2DSPHERE);


        DBCollection collection = Mockito.mock(DBCollection.class);

        DB db = Mockito.mock(DB.class);

        Mockito.when(mongo.getDB(Mockito.anyString())).thenReturn(db);
        Mockito.when(db.getCollection(Mockito.anyString())).thenReturn(collection);

        action.runAction(connector, mongo, "myDB");
    }

}
