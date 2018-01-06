package ox.engine.internal;

import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.structure.OrderingType;
import com.mongodb.Mongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Fernando Nogueira
 * @since 4/28/14 4:16 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateCreateIndexRecreateIfAlreadyExistsTest {

    @Mock
    private MigrationEnvironment env;

    @Mock
    private MongoDBConnector connector;

    @Mock
    private Mongo mongo;

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateIndexRecreateIfAlreadyExists() throws InvalidMigrateActionException {

        Mockito
                .when(connector
                        .verifyIfHasSameNameAndDifferentAttributes(
                                Mockito.anyMap(),
                                Mockito.anyString(),
                                Mockito.anyString()))
                .thenReturn(true);

        MigrateAction
                .createIndex("myIndex")
                .setCollection("myColl")
                .recreateIfNotEquals()
                .addAttribute("attr1", OrderingType.GEO_2DSPHERE)
                .runAction(connector, mongo, "myDB");
    }

}