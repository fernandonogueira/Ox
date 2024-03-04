package ox.integration.dropindex;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.integration.base.OxBaseContainerTest;

import java.util.ArrayList;
import java.util.List;

public class DropIndexByNameTest extends OxBaseContainerTest {

    @Test
    public void shouldDropIndexByName() throws InvalidMongoConfiguration {
        OxConfig config = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName("drop_index_test_db")
                .scanPackage("ox.integration.dropindex.migrations")
                .build();

        Ox ox = Ox.setUp(config);
        ox.up(1);

        Assertions.assertThat(ox.databaseVersion()).isEqualTo(1);

        List<Document> allIndexes = getDefaultMongo()
                .getDatabase(config.databaseName())
                .getCollection("drop_index_test")
                .listIndexes().into(new ArrayList<>());
        Assertions.assertThat(allIndexes).hasSize(2);
        Assertions.assertThat(allIndexes.get(1).get("name")).isEqualTo("email_index");

        ox.up();

        Assertions.assertThat(ox.databaseVersion()).isEqualTo(2);
        allIndexes = getDefaultMongo()
                .getDatabase(config.databaseName())
                .getCollection("drop_index_test")
                .listIndexes().into(new ArrayList<>());
        Assertions.assertThat(allIndexes).hasSize(1);
        Assertions.assertThatList(allIndexes).extracting("name")
                .doesNotHave(new Condition<>(n -> n.equals("email_index"), "email_index"));

    }
}
