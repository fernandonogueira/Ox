.. _installation:

Installation
============

To install Ox is simple:

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
       compile 'com.github.fernandonogueira:ox:x.y.z'
   }

   repositories {
       maven {
           url 'https://jitpack.io'
       }
   }