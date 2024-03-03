.. _installation:

Installation
============

Ox is available on `JitPack <https://jitpack.io>`_.

Maven
-----

.. code-block:: xml

   <dependency>
        <groupId>com.github.fernandonogueira</groupId>
        <artifactId>ox</artifactId>
        <version>x.y.z</version>
   </dependency>

   <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
   </repositories>


Gradle
------

.. code-block:: groovy

   dependencies {
     implementation 'com.github.fernandonogueira:Ox:x.y.z'
   }

   repositories {
     maven { url 'https://jitpack.io' }
   }
