package ox.integration;

import com.mongodb.MongoClient;
import org.junit.Test;
import ox.engine.Ox;
import ox.engine.OxConfig;
import ox.integration.base.OxBaseContainerTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RollbackTest extends OxBaseContainerTest {

    @Test
    public void shouldRollback() {

        MongoClient mongo = getDefaultMongo();

        OxConfig config = OxConfig.builder()
                .mongo(mongo)
                .databaseName("rollback-test")
                .scanPackage("ox.db.migrations")
                .build();

        Ox ox = Ox.configure(config);

        ox.up();
        assertThat(ox.databaseVersion()).isEqualTo(10);
        ox.down(2);
        assertThat(ox.databaseVersion()).isEqualTo(2);
    }

}
