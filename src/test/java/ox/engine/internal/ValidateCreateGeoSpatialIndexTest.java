package ox.engine.internal;

import com.mongodb.Mongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.structure.OrderingType;

@RunWith(MockitoJUnitRunner.class)
public class ValidateCreateGeoSpatialIndexTest {

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

        action.runAction(connector, mongo, "myDB");
    }

}
