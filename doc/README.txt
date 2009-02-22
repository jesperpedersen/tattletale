JBoss Tattletale
================

JBoss Tattletale is a tool that can help you get an overview of the project you are working on
or a product that you depend on.

The tool will provide you with reports that can help you

* Identify dependencies between JAR files
* Find missing classes from the classpath
* Spot if a class is located in multiple JAR files
* Spot if the same JAR file is located in multiple locations
* With a list of what each JAR file requires and provides
* Verify the SerialVersionUID of a class

JBoss Tattletale will recursive scan the directory pass as the argument for JAR files and then
build the reports as HTML files.

The main HTML file is: index.html

JBoss Tattletale is licensed under GNU Lesser General Public License (LGPL) version 2.1 or later.

We hope that JBoss Tattletale will help you in your development tasks ! 


Running the client:
-------------------
java -Xmx512m -jar jboss-tattletale.jar <scan-directory> [output-directory]

Output: Analysis reports generate in current directory if no output-directory set.

Ant Integration:
-----------------
TattleTale can be executed in ant build scripts.

* Add jboss-tattletale.jar and javassist.jar to the ant classpath
* Add xmlns:tattletale="antlib:org.jboss.tattletale.ant"> to your <project.. tag
* Usage: <tattletale:report scanDir="" outputDir=""/>

ClassLoader structure:
----------------------
JBoss Tattletale include functionality to identify certain classloader structures.
The plugins must implement the 

 org.jboss.tattletale.reporting.classloader.ClassLoaderStructure

interface and contain a default no-argument constructor.


Current plugins:

* org.jboss.tattletale.reporting.classloader.NoopClassLoaderStructure

  A no operation plugin that always will include the queried archive in the report

* org.jboss.tattletale.reporting.classloader.JBossAS4ClassLoaderStructure

  Plugin for the JBoss Application Server 4.x series

* org.jboss.tattletale.reporting.classloader.JBossAS5ClassLoaderStructure

  Plugin for the JBoss Application Server 5.x series


The plugin is loaded through the 'classloader' key in jboss-tattletale.properties file.

NOTE: This feature is currently based on directory structures and may therefore fail to
identify archives that should be included in the reports. If you want to be sure that all
archives are included use the NoopClassLoaderStructure plugin.

Development:
------------
Home          : http://www.jboss.org/projects/tattletale
Download      : http://www.jboss.org/projects/tattletale
Forum         : http://www.jboss.org/index.html?module=bb&op=viewforum&f=306
Issue tracking: http://jira.jboss.com/jira/browse/TTALE
AnonSVN       : http://anonsvn.jboss.org/repos/tattletale
Developer SVN : https://svn.jboss.org/repos/tattletale
