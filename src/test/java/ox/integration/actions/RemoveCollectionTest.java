package ox.integration.actions;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.integration.base.OxBaseContainerTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveCollectionTest extends OxBaseContainerTest {

    @Test
    public void remoteCollectionTest() throws InvalidMongoConfiguration {
        MongoClient mongo = getDefaultMongo();
        Ox ox = Ox.configure(
                mongo,
                "ox.integration.actions.migrations",
                "removeCollectionTest");

        mongo.getDatabase("removeCollectionTest")
                .getCollection("collection1")
                .insertOne(new Document("attr1", 123));

        ox.up();
        assertThat(ox.databaseVersion()).isEqualTo(1);
    }

}
