package ox.integration;

import com.mongodb.MongoClient;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.integration.base.OxBaseContainerTest;

import java.util.ArrayList;
import java.util.List;

public class CreateIndexTest extends OxBaseContainerTest {

    @Test
    public void shouldCreateIndex() {
        MongoClient mongo = getDefaultMongo();
        OxConfig config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("create-index-test")
                .scanPackage("ox.db.migrations")
                .build();

        Ox ox = Ox.configure(config);
        ox.up(1);

        List<Document> allIndexes = getDefaultMongo()
                .getDatabase(config.databaseName())
                .getCollection("myCollection")
                .listIndexes().into(new ArrayList<>())
                .stream().filter(d -> !d.getString("name").equals("_id_")).toList();

        Assertions.assertThatCollection(allIndexes)
                .extracting("name")
                .containsExactlyInAnyOrder("myIndex", "myIndex2");

    }
}
