package ox.engine;

import ox.Configuration;

public record OxCollectionsConfig(
        boolean createMigrationCollection,
        String migrationCollectionName,
        boolean createLockCollection,
        String lockCollectionName) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        boolean createMigrationCollection = true;
        String migrationCollectionName = Configuration.SCHEMA_VERSION_COLLECTION_NAME;
        boolean createLockCollection = true;
        String lockCollectionName = Configuration.LOCK_COLLECTION_NAME;

        public Builder createMigrationCollection(boolean createMigrationCollection) {
            this.createMigrationCollection = createMigrationCollection;
            return this;
        }

        public Builder migrationCollectionName(String migrationCollectionName) {
            this.migrationCollectionName = migrationCollectionName;
            return this;
        }

        public Builder createLockCollection(boolean createLockCollection) {
            this.createLockCollection = createLockCollection;
            return this;
        }

        public Builder lockCollectionName(String lockCollectionName) {
            this.lockCollectionName = lockCollectionName;
            return this;
        }

        public OxCollectionsConfig build() {
            return new OxCollectionsConfig(
                    createMigrationCollection,
                    migrationCollectionName,
                    createLockCollection,
                    lockCollectionName
            );
        }

    }

}
