package ox.integration.ifnotexists;

import com.mongodb.MongoClient;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.integration.base.OxBaseContainerTest;

import java.util.ArrayList;
import java.util.List;

public class CreateIfNotExistsIndexTest extends OxBaseContainerTest {

    /**
     * simplest case of creating an index
     */
    @Test
    public void shouldCreateIndex() throws InvalidMongoConfiguration {
        MongoClient mongo = getDefaultMongo();

        OxConfig config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("create-index-if-not-exists-test")
                .scanPackage("ox.integration.ifnotexists.migrations")
                .build();

        List<Document> allIndexes = getDefaultMongo()
                .getDatabase(config.databaseName())
                .getCollection("myCollection")
                .listIndexes().into(new ArrayList<>());

        Assertions.assertThat(allIndexes).isEmpty();

        Ox ox = Ox.configure(config);
        ox.up();

        allIndexes = getDefaultMongo()
                .getDatabase(config.databaseName())
                .getCollection("myCollection")
                .listIndexes().into(new ArrayList<>())
                .stream().filter(d -> !d.getString("name").equals("_id_")).toList();

        Assertions.assertThatCollection(allIndexes)
                .extracting("name")
                .containsExactlyInAnyOrder("myIndex");

    }
}
