package ox.engine;

public record OxConfigExtras(
        boolean failOnMissingCollection,
        boolean dryRun,
        long lockTTLSeconds) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        boolean failOnMissingCollection = false;
        boolean dryRun = false;
        long lockTTLSeconds = 180;

        public Builder failOnMissingCollection(boolean failOnMissingCollection) {
            this.failOnMissingCollection = failOnMissingCollection;
            return this;
        }

        public Builder dryRun() {
            this.dryRun = true;
            return this;
        }

        public Builder lockTTLSeconds(long lockTTLSeconds) {
            this.lockTTLSeconds = lockTTLSeconds;
            return this;
        }

        public OxConfigExtras build() {
            return new OxConfigExtras(
                    failOnMissingCollection,
                    dryRun,
                    lockTTLSeconds
            );
        }

    }
}
