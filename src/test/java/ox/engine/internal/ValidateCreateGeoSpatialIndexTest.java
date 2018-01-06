package ox.engine.internal;

import ox.engine.structure.OrderingType;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Fernando Nogueira
 * @since 4/19/14 6:11 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateCreateGeoSpatialIndexTest {

    @InjectMocks
    private CreateIndexAction action;

    @Mock
    private MigrationEnvironment environment;

    @Mock
    private Mongo mongo;

    @Mock
    private MongoDBConnector connector;

    @Test
    public void createIndexGeospatialType() {
        MigrateAction action = MigrateAction
                .createIndex("IndexName")
                .setCollection("myCollection")
                .addAttribute("myPositionAttr", OrderingType.GEO_2DSPHERE);
    }

    @Test
    public void generateGeospatialIndexCommand() {
        MigrateAction action = MigrateAction
                .createIndex("IndexName")
                .setCollection("myCollection")
                .addAttribute("myPositionAttr", OrderingType.GEO_2DSPHERE);

        action.toString();
    }

    @Test
    public void runActionToCreateGeospatialIndex() {
        CreateIndexAction action = MigrateAction
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
