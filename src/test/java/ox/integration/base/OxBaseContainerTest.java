package ox.integration.base;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class OxBaseContainerTest {

    @ClassRule
    public static MongoDBContainer mongoContainer = new MongoDBContainer(DockerImageName.parse("mongo:5"));

    private MongoClient mongoClient;

    @Before
    public void setUp() {
        mongoClient = new MongoClient(
                new ServerAddress(mongoContainer.getHost(), mongoContainer.getFirstMappedPort()),
                MongoClientOptions.builder().connectTimeout(10000).socketTimeout(10000).build()
        );
    }

    public MongoClient getDefaultMongo() {
        return mongoClient;
    }

    @After
    public void shutdown() {
        mongoClient.close();
    }

}
