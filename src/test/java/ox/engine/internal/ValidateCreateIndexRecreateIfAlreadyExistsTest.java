package ox.engine.internal;

import com.mongodb.MongoClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.structure.OrderingType;

@RunWith(MockitoJUnitRunner.class)
public class ValidateCreateIndexRecreateIfAlreadyExistsTest {

    @Mock
    private OxEnvironment env;

    @Mock
    private MongoDBConnector connector;

    @Mock
    private MongoClient mongo;

    @Test
    public void testCreateIndexRecreateIfAlreadyExists() {

        Mockito
                .when(connector
                        .verifyIfHasSameNameAndDifferentAttributes(
                                Mockito.anyMap(),
                                Mockito.anyString(),
                                Mockito.anyString()))
                .thenReturn(true);

        OxAction
                .createIndex("myIndex")
                .setCollection("myColl")
                .recreateIfNotEquals()
                .addAttribute("attr1", OrderingType.GEO_2DSPHERE)
                .runAction(connector, mongo, "myDB");
    }

}
