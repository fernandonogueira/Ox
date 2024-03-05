package ox.engine.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidMigrateActionException;
import ox.engine.structure.OrderingType;

@RunWith(MockitoJUnitRunner.class)
public class OxEnvironmentTest {

    @InjectMocks
    private OxEnvironment e = new OxEnvironmentImpl();

    @Mock
    private MongoDBConnector mongoConnector;

    /**
     * Validates the OxEnvironment.execute() method
     *
     */
    @Test
    public void environmentTest() throws InvalidMigrateActionException {

        OxAction action = OxAction
                .createIndex("IndexName")
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.ASC)
                .addAttribute("attr2", OrderingType.ASC);

        e.execute(action);

    }
}
