package ox.integration.base;

import com.mongodb.MongoClient;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;

public abstract class OxBaseContainerTest {

    @ClassRule
    public static GenericContainer mongoContainer = new GenericContainer<>("mongo:3.6")
            .withExposedPorts(27017);

    private MongoClient mongoClient;

    @Before
    public void setUp() {
        mongoClient = new MongoClient(
                mongoContainer.getContainerIpAddress(),
                mongoContainer.getMappedPort(27017)
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
