package ox.migrationscollection;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxCollectionsConfig;
import ox.engine.OxConfig;
import ox.engine.exception.InvalidMongoClientConfiguration;
import ox.engine.exception.MissingMigrationHistoryCollectionException;
import ox.integration.base.OxBaseContainerTest;

public class DoNotCreateMigrationsCollectionTest extends OxBaseContainerTest {

    @Test(expected = MissingMigrationHistoryCollectionException.class)
    public void shouldNotCreateMigrationsCollection() {

        MongoClient mongo = getDefaultMongo();
        mongo.getDatabase("withoutSchemaMigrationsCollection")
                .getCollection("someTest")
                .insertOne(new Document("test", true));

        OxConfig config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("withoutSchemaMigrationsCollection")
                .scanPackage("ox.db.migrations")
                .collectionsConfig(OxCollectionsConfig.builder()
                        .createMigrationCollection(false)
                        .build())
                .build();

        Ox ox = Ox.configure(config);

        ox.databaseVersion();

    }
}
