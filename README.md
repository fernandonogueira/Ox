Ox - MongoDB Database Version Migration Tool
===================

## Description
Schemaless doesn't mean no schema!

Control your MongoDB schema with ease.
## Usage

#### Creating migrations...

```java
public class V1_CreateIndexMigrationTest implements Migration {

    @Override
    public void up(MigrationEnvironment env) {

        MigrateAction
                .createIndex("myIndex")
                .setCollection("myCollection")
                .addAttribute("attr1", OrderType.ASC)
                .execute(env);

        MigrateAction
                .createIndex("myIndex")
                .setCollection("myCollection")
                .addAttribute("attr2", OrderType.DESC)
                .addAttribute("attr3", OrderType.ASC)
                .execute(env);

    }

    @Override
    public void down(MigrationEnvironment env) {
        ...
    }
}
```

#### Executing all migrations... :)


```java

public void myMethod(){
   ...

   MongoClient mongo = new MongoClient("myDatabaseHost");
   MigratorEngine.setUp(mongo, "ox.db.migrates", "myDatabaseName").up();

   ...
}

```

## Requirements

- Java 7
- Mongo Java Driver or Spring Data MongoDB 1.0 - 1.4 (Tested with Spring Data MongoDB 1.4.1)

## Dependencies

- Guava Library
- Mongo Java Driver
- Log4j

That' all folks. :)
