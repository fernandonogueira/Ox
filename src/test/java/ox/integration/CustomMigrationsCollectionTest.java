package ox.integration;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxCollectionsConfig;
import ox.engine.OxConfig;
import ox.integration.base.OxBaseContainerTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class CustomMigrationsCollectionTest extends OxBaseContainerTest {

    @Test
    public void shouldCreateCustomMigrationsCollection() {

        String customCollectionName = "ox_schema_migration_history_custom";

        OxConfig config = OxConfig.builder()
                .mongo(getDefaultMongo())
                .databaseName("custom_migrations_db")
                .scanPackage("ox.db.migrations")
                .collectionsConfig(OxCollectionsConfig.builder()
                        .migrationCollectionName(customCollectionName)
                        .build())
                .build();

        Ox.configure(config).up();

        MongoDatabase database = getDefaultMongo().getDatabase("custom_migrations_db");

        Set<String> set = new HashSet<>();
        MongoIterable<String> collections = database.listCollectionNames();
        collections.into(set);

        assertTrue(set.contains(customCollectionName));
    }

}
