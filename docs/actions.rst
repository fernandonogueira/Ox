Actions
=======

An Ox migration is composed of one or more actions.
The types of actions are described in this page.

OxAction
--------

All actions should be created using the OxAction class. This class also have a ``setCollection`` method
that all actions will use to know which collection will be modified.

Create Index
------------


Action used to create a new index or to ensure that the index is created.

Example:

.. code-block:: java

    OxAction
        .createIndex("index_name")
        .setCollection("collection_name")
        .addAttribute("attr1", OrderingType.ASC)
        .build()

This action supports a lot of create index options supported by MongoDB database.

Supported options:

+----------------------------+-----------------------------------------------------+
| Option                     | Description                                         |
+----------------------------+-----------------------------------------------------+
| indexName                  | The index name                                      |
+----------------------------+-----------------------------------------------------+
| ifNotExists                | Creates the index only if it do not exists yet      |
+----------------------------+-----------------------------------------------------+
| unique                     | Creates a unique index                              |
+----------------------------+-----------------------------------------------------+
| recreateIfNotEquals        | Recreates the index if not equals definition        |
+----------------------------+-----------------------------------------------------+
| ttlIndex                   | Sets the index as TTL index                         |
+----------------------------+-----------------------------------------------------+
| ttlIndexExpireAfterSeconds | The time-to-live, if it is a TTL index.             |
+----------------------------+-----------------------------------------------------+

It's worth mentioning that the ``recreateIfNotEquals`` option.
This option ensure that the described index (with the given name) should have the same attributes
or it will be deleted and re-created.


Remove Collection
-----------------

Used to remove a collection.


Remove Index
------------

Used to remove an index.

Custom Actions/Modifying Data
-----------------------------

If you want to modify data or create a custom action,
you should use the `OxEnvironment` that is passed as parameter to `up` and `down` methods described in your migration classes.

E.g.

.. code-block:: java

    public class V0001__first_migration implements Migration {

        @Override
        public void up(OxEnvironment env) {

            MongoDatabase db = env.getMongoDatabase();
            // do something with the database

        }

        @Override
        public void down(OxEnvironment env) {
            // undo something
        }

    }
