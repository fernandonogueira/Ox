package ox.integration;

import com.mongodb.client.ListIndexesIterable;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.engine.OxConfigExtras;
import ox.engine.exception.InvalidMongoConfiguration;
import ox.integration.base.OxBaseContainerTest;

import java.util.ArrayList;
import java.util.List;

public class DryRunTest extends OxBaseContainerTest {

    @Test
    public void dryRunUpTest() throws InvalidMongoConfiguration {
        OxConfig config = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName("up_dry_run_db")
                .scanPackage("ox.db.migrations")
                .extras(OxConfigExtras.builder().dryRun().build())
                .build();

        Ox.configure(config).up();

        ListIndexesIterable<Document> indexes = getDefaultMongo()
                .getDatabase("up_dry_run_db")
                .getCollection(config.collectionsConfig().migrationCollectionName())
                .listIndexes();

        List<Document> indexesList = indexes.into(new ArrayList<>())
                .stream().filter(d -> !d.getString("name").equals("_id_"))
                .toList();
        Assertions.assertThat(indexesList).isEmpty();
    }

    @Test
    public void dryRunDownTest() throws InvalidMongoConfiguration {
        OxConfig config = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName("up_dry_run_db")
                .scanPackage("ox.db.migrations")
                .extras(OxConfigExtras.builder().dryRun().build())
                .build();

        Ox.configure(config).down();

        ListIndexesIterable<Document> indexes = getDefaultMongo()
                .getDatabase("up_dry_run_db")
                .getCollection(config.collectionsConfig().migrationCollectionName())
                .listIndexes();

        List<Document> indexesList = indexes.into(new ArrayList<>())
                .stream().filter(d -> !d.getString("name").equals("_id_"))
                .toList();
        Assertions.assertThat(indexesList).isEmpty();
    }
}
