package ox.utils;

import com.mongodb.Mongo;
import org.mockito.Mockito;
import ox.engine.internal.MongoDBConnector;
import ox.engine.internal.MongoDBConnectorConfig;

public class TestUtils {

    public static MongoDBConnector newMongoDBConnector() {
        Mongo mockedMongo = Mockito.mock(Mongo.class);
        return new MongoDBConnector(MongoDBConnectorConfig
                .create()
                .setMongoClient(mockedMongo)
                .setDatabaseName(Faker.fakeDBName()));
    }
}
