package ox.engine.internal;

import ox.engine.internal.resources.Location;
import ox.engine.internal.resources.scanner.Scanner;
import ox.engine.structure.Migration;
import ox.utils.logging.Logger;
import ox.utils.logging.Loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MigrationResolver {

    private static final Logger LOG = Loggers.getLogger(MigrationResolver.class);

    public List<ResolvedMigration> resolveMigrations(String scanPackage)
            throws Exception {

        Scanner scanner = new Scanner(Thread.currentThread().getContextClassLoader());
        Class<?>[] resources = scanner.scanForClasses(new Location(scanPackage), Migration.class);

        List<ResolvedMigration> resolvedMigrations = new ArrayList<>();

        if (resources == null) {
            return resolvedMigrations;
        }

        for (Class<?> resource : resources) {
            if (Migration.class.isAssignableFrom(resource)) {
                Pattern pattern = Pattern.compile("V\\d*_");
                Matcher matcher = pattern.matcher(resource.getCanonicalName());
                if (matcher.find()) {
                    String string = matcher.group();

                    ResolvedMigration resolvedMigration = new ResolvedMigration();
                    Migration migration = ((Class<Migration>) resource).newInstance();
                    resolvedMigration.setMigrate(migration);

                    Integer version = Integer.valueOf(string.substring(1, string.length() - 1));
                    resolvedMigration.setVersion(version);

                    resolvedMigrations.add(resolvedMigration);

                    LOG.info("[Ox] Resolved Migrate Found: " + resolvedMigration);
                }
            }
        }

        return resolvedMigrations;
    }

}
