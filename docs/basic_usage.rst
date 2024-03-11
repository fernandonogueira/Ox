Basic Usage
===========

Prerequisites
-------------

Ox requires Java 17+ and Mongo Java Driver

Usage
-----

* Add Ox to your project as you can see in the :ref:`installation` page.

* Then create a package where you will store your migration classes. Like ``your.application.package.ox.migrations``.

* Create your first migration:

.. code-block:: java

    package ox.samples.spring.ox.migrations;

    import ox.engine.exception.OxException;
    import ox.engine.internal.OxAction;
    import ox.engine.internal.OxEnvironment;
    import ox.engine.structure.Migration;
    import ox.engine.structure.OrderingType;

    public class V0001__your_migration_description implements Migration {

        @Override
        public void up(OxEnvironment oxEnvironment) throws OxException {
            oxEnvironment.execute(OxAction
                    .createIndex("your_index_name")
                    .setCollection("yourCollection")
                    .addAttribute("attr1", OrderingType.ASC));
        }

        @Override
        public void down(OxEnvironment oxEnvironment) throws OxException {

            oxEnvironment.execute(OxAction
                    .removeIndex("your_index_name")
                    .ifExists()
                    .setCollection("yourCollection"));

        }

    }

* Run Ox during your application initialization or in another process before app initialization.

.. code-block:: java

    public Ox init(MongoClient mongo) {

        Ox ox = Ox.configure(mongo, "your.application.package.configs.OxConfig", "yourDatabaseName");
        ox.up();
        return ox;

    }
