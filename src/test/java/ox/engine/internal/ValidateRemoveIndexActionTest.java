package ox.engine.internal;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidMigrateActionException;
import ox.utils.Faker;

@RunWith(MockitoJUnitRunner.class)
public class ValidateRemoveIndexActionTest {

    private MongoDBConnector newMongoDBConnector() {
        MongoClient mockedMongo = Mockito.mock(MongoClient.class);
        return new MongoDBConnector(MongoDBConnectorConfig
                .builder()
                .setMongoClient(mockedMongo)
                .setDatabaseName(Faker.fakeDBName())
                .build());
    }


    /**
     * No collection set
     *
     * @throws InvalidMigrateActionException
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void validateRemoveIndexAction() throws InvalidMigrateActionException {
        OxAction
                .removeIndex("someIndex")
                .validateAction();
    }

    /**
     * No index name set
     *
     * @throws InvalidMigrateActionException
     */
    @Test(expected = InvalidMigrateActionException.class)
    public void validateRemoveIndexActionWithoutIndexName() throws InvalidMigrateActionException {
        OxAction
                .removeIndex(null)
                .validateAction();
    }

    /**
     * Happy day!
     *
     * @throws InvalidMigrateActionException
     */
    @Test
    public void validateRemoveIndexActionHappyDay() throws InvalidMigrateActionException {
        OxAction
                .removeIndex("correctIndexName")
                .setCollection("collectionSet")
                .validateAction();
    }

}
