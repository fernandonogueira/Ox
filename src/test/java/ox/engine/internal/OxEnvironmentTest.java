package ox.engine.internal;

import ox.engine.exception.InvalidMongoConfiguration;
import ox.engine.structure.OrderingType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Fernando Nogueira
 * @since 4/29/14 11:33 AM
 */
@RunWith(MockitoJUnitRunner.class)
public class OxEnvironmentTest {

    @InjectMocks
    private OxEnvironment e = new OxEnvironment();

    @Mock
    private MongoDBConnector mongoConnector;

    /**
     * Validates the OxEnvironment.execute() method
     *
     * @throws InvalidMongoConfiguration
     */
    @Test
    public void environmentTest() throws InvalidMongoConfiguration {

        OxAction action = OxAction
                .createIndex("IndexName")
                .setCollection("myCollection")
                .addAttribute("attr1", OrderingType.ASC)
                .addAttribute("attr2", OrderingType.ASC);

        e.execute(action);

    }
}
