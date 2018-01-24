package ox.engine.internal;

import com.mongodb.Mongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ox.engine.exception.IndexAlreadyExistsException;
import ox.engine.structure.OrderingType;

/**
 * @author Fernando Nogueira
 * @since 4/19/14 7:38 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateIndexAlreadyExistsExceptionTest {

    @InjectMocks
    private CreateIndexAction action;

    @Mock
    private Mongo mongo;

    @Mock
    private MongoDBConnector connector;

    @SuppressWarnings("unchecked")
    @Test(expected = IndexAlreadyExistsException.class)
    public void validateIndexAlreadyExistsException() {

        action =
                OxAction
                        .createIndex("myIndexTest")
                        .setCollection("myCollectionTest")
                        .addAttribute("anyAttr", OrderingType.GEO_2DSPHERE);


        Mockito.when(
                connector
                        .verifyIfIndexExists(
                                Mockito.anyMap(),
                                Mockito.anyString(),
                                Mockito.anyString()))
                .thenReturn(true);

        action.runAction(connector, mongo, "myDatabase");
    }

}
