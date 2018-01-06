/**
 * Copyright 2010-2016 Boxfuse GmbH
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ox.engine.internal.resources.scanner;


import ox.engine.exception.GenericMongoMigrationRuntimeException;
import ox.engine.internal.resources.Location;
import ox.engine.internal.resources.Resource;
import ox.engine.internal.resources.classpathscanner.ClassPathScanner;
import ox.engine.internal.resources.filesystem.FileSystemScanner;

/**
 * Scanner for Resources and Classes.
 */
public class Scanner {
    private final ResourceAndClassScanner resourceAndClassScanner;

    private final ClassLoader classLoader;
    private final FileSystemScanner fileSystemScanner = new FileSystemScanner();

    public Scanner(ClassLoader classLoader) {
        this.classLoader = classLoader;
        resourceAndClassScanner = new ClassPathScanner(classLoader);
    }

    /**
     * Scans this location for resources, starting with the specified prefix and ending with the specified suffix.
     *
     * @param location The location to start searching. Subdirectories are also searched.
     * @param prefix   The prefix of the resource names to match.
     * @param suffix   The suffix of the resource names to match.
     * @return The resources that were found.
     */
    public Resource[] scanForResources(Location location, String prefix, String suffix) {
        try {
            if (location.isFileSystem()) {
                return fileSystemScanner.scanForResources(location, prefix, suffix);
            }
            return resourceAndClassScanner.scanForResources(location, prefix, suffix);
        } catch (Exception e) {
            throw new GenericMongoMigrationRuntimeException("Unable to scan for SQL migrations in location: " + location, e);
        }
    }


    /**
     * Scans the classpath for concrete classes under the specified package implementing this interface.
     * Non-instantiable abstract classes are filtered out.
     *
     * @param location             The location (package) in the classpath to start scanning.
     *                             Subpackages are also scanned.
     * @param implementedInterface The interface the matching classes should implement.
     * @return The non-abstract classes that were found.
     * @throws Exception when the location could not be scanned.
     */
    public Class<?>[] scanForClasses(Location location, Class<?> implementedInterface) throws Exception {
        return resourceAndClassScanner.scanForClasses(location, implementedInterface);
    }

    /**
     * @return The class loader used for scanning.
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }
}