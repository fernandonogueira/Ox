package ox.utils;

import com.mongodb.MongoClient;
import org.mockito.Mockito;
import ox.engine.internal.MongoDBConnector;
import ox.engine.internal.MongoDBConnectorConfig;

public class TestUtils {

    public static MongoDBConnector newMongoDBConnector() {
        MongoClient mockedMongo = Mockito.mock(MongoClient.class);

        MongoDBConnectorConfig config = MongoDBConnectorConfig.builder()
                .setMongoClient(mockedMongo)
                .setDatabaseName(Faker.fakeDBName())
                .build();

        return new MongoDBConnector(config);
    }
}
