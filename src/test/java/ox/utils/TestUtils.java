package ox.utils;

import com.mongodb.MongoClient;
import org.mockito.Mockito;
import ox.engine.OxConfig;
import ox.engine.internal.MongoDBConnector;
import ox.engine.internal.MongoDBConnectorConfig;

public class TestUtils {

    public static MongoDBConnector newMongoDBConnector() {
        MongoClient mockedMongo = Mockito.mock(MongoClient.class);

        OxConfig oxConfig = OxConfig.builder()
                .mongo(mockedMongo)
                .databaseName(Faker.fakeDBName())
                .build();

        MongoDBConnectorConfig config = MongoDBConnectorConfig.fromOxConfig(oxConfig);

        return new MongoDBConnector(config);
    }
}
