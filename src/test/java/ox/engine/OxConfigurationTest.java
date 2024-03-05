package ox.engine;

import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ox.engine.exception.InvalidMongoClientConfiguration;
import ox.engine.exception.InvalidReadPreferenceException;

@RunWith(MockitoJUnitRunner.class)
public class OxConfigurationTest {

    @Test(expected = InvalidMongoClientConfiguration.class)
    public void validateInvalidMongoInstance() {
        Ox.configure(null, "ox.db.migrations", "myDB").up();
    }

    @Test(expected = InvalidReadPreferenceException.class)
    public void validateInvalidReadPreference() {
        MongoClient mongo = Mockito.mock(MongoClient.class);
        Mockito.when(mongo.getReadPreference()).thenReturn(ReadPreference.secondaryPreferred());

        Ox.configure(mongo, "ox.db.migrations", "myDB").up();
    }


}
