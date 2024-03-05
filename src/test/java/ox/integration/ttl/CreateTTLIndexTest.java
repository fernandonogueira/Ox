package ox.integration.ttl;

import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.integration.base.OxBaseContainerTest;

import java.util.ArrayList;
import java.util.List;

public class CreateTTLIndexTest extends OxBaseContainerTest {

    @Test
    public void shouldCreateTTLIndex() throws InvalidMongoConfiguration {

        OxConfig config = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName("create-ttl-index-test")
                .scanPackage("ox.integration.ttl.migrations")
                .build();

        Ox ox = Ox.configure(config);
        ox.up();

        List<Document> allIndexes = getDefaultMongo()
                .getDatabase(config.databaseName())
                .getCollection("ttl_collection")
                .listIndexes().into(new ArrayList<>())
                .stream().filter(d -> !d.getString("name").equals("_id_")).toList();

        Assertions.assertThatCollection(allIndexes)
                .extracting("name")
                .containsExactlyInAnyOrder("ttl_index");

        Assertions.assertThat(allIndexes.get(0).get("expireAfterSeconds")).isEqualTo(600);

    }
}
