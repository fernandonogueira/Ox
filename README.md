Ox - MongoDB Database Versioning and Migration Tool
===================

[![Build Status][travis-badge]][travis-url] [![Code Coverage][codecov-badge]][codecov-url] [![Artifacts][jitpack-badge]][jitpack-url]

## Description
Schemaless doesn't mean no schema!

Control and migrate your MongoDB schema versions with ease, ensuring that indexes are created right and that your data is exactly as you expect.

MongoDB is a NoSQL, Document-based, database but it also need some data migration in some cases, specially 
when your application uses a multi-instance architecture and you must upgrade or change data in all your databases.

Feel free to contribute and ask changes to this project.
Pull Requests are also welcome and will be approved quickly.

This project is mature. It is used since 2014 in production but it wasn't public since then.

## Motivation

When you have to manage a lot of MongoDB databases it may consume a lot of time when you need to keep your indexes and documents in the right version.

This project simplifies that. 

## Usage

#### Creating migrations...

```java
public class V0001__CreateIndexMigrationTest implements Migration {

    @Override
    public void up(MigrationEnvironment env) {

        OxAction
                .createIndex("myIndex")
                .setCollection("myCollection")
                .addAttribute("attr1", OrderType.ASC)
                .execute(env);

        OxAction
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
   Ox.setUp(mongo, "ox.db.migrates", "myDatabaseName").up();

   ...
}

```

## Requirements

- Java 7
- Mongo Java Driver 3.x

## Dependencies

- SLF4j

That' all folks. :)

## LICENSE

```
MIT License

Copyright (c) 2018 Ox Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

[codecov-badge]: https://codecov.io/gh/fernandonogueira/Ox/branch/master/graph/badge.svg
[codecov-url]: https://codecov.io/gh/fernandonogueira/Ox
[travis-badge]: https://travis-ci.org/fernandonogueira/Ox.svg?branch=master
[travis-url]: https://travis-ci.org/fernandonogueira/Ox
[jitpack-badge]: https://jitpack.io/v/fernandonogueira/Ox.svg
[jitpack-url]: https://jitpack.io/#fernandonogueira/Ox