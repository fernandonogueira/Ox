package ox.integration.duplicate;

import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.integration.base.OxBaseContainerTest;
import ox.utils.Faker;

import java.util.ArrayList;
import java.util.List;

public class DuplicateIndexTest extends OxBaseContainerTest {

    /**
     * Tries to create a index that already exists.
     * (Same attributes)
     * <p/>
     * The index must not be created. This action should be ignored.
     */
    @Test
    public void shouldIgnoreDuplicateIndex() {

        String pkg = "ox.integration.duplicate.migrations";
        String collection = "test_collection";
        OxConfig config = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName(Faker.fakeDBName())
                .scanPackage(pkg)
                .build();

        // Should have no index
        List<Document> allIndexes = getDefaultMongo()
                .getDatabase(config.databaseName())
                .getCollection(collection)
                .listIndexes().into(new ArrayList<>());

        Assertions.assertThat(allIndexes).isEmpty();

        Ox ox = Ox.configure(config);
        ox.up(1);

        allIndexes = getDefaultMongo()
                .getDatabase(config.databaseName())
                .getCollection(collection)
                .listIndexes().into(new ArrayList<>());

        Assertions.assertThat(allIndexes).hasSize(2);

        ox.up();

        allIndexes = getDefaultMongo()
                .getDatabase(config.databaseName())
                .getCollection(collection)
                .listIndexes().into(new ArrayList<>());

        Assertions.assertThat(allIndexes).hasSize(2);

    }

}
