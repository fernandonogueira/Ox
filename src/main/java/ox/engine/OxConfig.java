package ox.engine;

import com.mongodb.MongoClient;

public record OxConfig(
        MongoClient mongo,
        String databaseName,
        String scanPackage,
        OxCollectionsConfig collectionsConfig,
        OxConfigExtras extras
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        MongoClient mongo;
        String databaseName;
        String scanPackage;
        OxCollectionsConfig collectionsConfig = OxCollectionsConfig.builder().build();
        OxConfigExtras extras = OxConfigExtras.builder().build();

        public Builder mongo(MongoClient mongo) {
            this.mongo = mongo;
            return this;
        }

        public Builder databaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Builder scanPackage(String scanPackage) {
            this.scanPackage = scanPackage;
            return this;
        }

        public Builder collectionsConfig(OxCollectionsConfig collectionsConfig) {
            this.collectionsConfig = collectionsConfig;
            return this;
        }

        public Builder extras(OxConfigExtras extras) {
            this.extras = extras;
            return this;
        }

        public OxConfig build() {
            return new OxConfig(
                    mongo,
                    databaseName,
                    scanPackage,
                    collectionsConfig,
                    extras
            );
        }

    }
}

