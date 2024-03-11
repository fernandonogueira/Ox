Actions
=======

An Ox migration is composed of one or more actions.
The types of actions are described on this page.

OxAction
--------

All actions should be created using the OxAction class. This class also has a mandatory ``setCollection`` method.

Create Index
------------


This action is used to create a new index or to ensure that the index is created.

Example:

.. code-block:: java

    OxAction
        .createIndex("index_name")
        .setCollection("collection_name")
        .addAttribute("attr1", OrderingType.ASC)
        .build()

This action supports several options supported by the MongoDB database.

Supported options:

+----------------------------+-----------------------------------------------------+
| Option                     | Description                                         |
+----------------------------+-----------------------------------------------------+
| indexName                  | The index name                                      |
+----------------------------+-----------------------------------------------------+
| ifNotExists                | Creates the index only if it does not exist yet     |
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
This option ensures that the described index (with the given name) should have the exact attributes, or it will be deleted and re-created.


Remove Index
------------

Removes an index by name.

Custom Actions/Modifying Data
-----------------------------

If you want to modify data or create a custom action,
you should use the `OxEnvironment` object passed as the parameter in the `up` and `down` methods described in your migration classes.

E.g.

.. code-block:: java

    public class V0001__first_migration implements Migration {

        @Override
        public void up(OxEnvironment env) {

            MongoDatabase db = env.getMongoDatabase();
            // apply your changes

        }

        @Override
        public void down(OxEnvironment env) {
            // undo changes
        }

    }
