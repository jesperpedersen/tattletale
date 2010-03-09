JBoss Tattletale
================

JBoss Tattletale is a tool that can help you get an overview of the project you are working on
or a product that you depend on.

The tool will provide you with reports that can help you

* Identify dependencies between JAR files
* Find missing classes from the classpath
* Spot if a class/package is located in multiple JAR files
* Spot if the same JAR file is located in multiple locations
* With a list of what each JAR file requires and provides
* Verify the SerialVersionUID of a class
* Find similar JAR files that have different version numbers
* Find JAR files without a version number
* Find unused JAR files
* Identify sealed / signed JAR archives
* Locate a class in a JAR file
* Get the OSGi status of your project

JBoss Tattletale will recursive scan the directory pass as the argument for JAR files and then
build the reports as HTML files.

The main HTML file is: index.html

JBoss Tattletale is licensed under GNU Lesser General Public License (LGPL) version 2.1 or later.

We hope that JBoss Tattletale will help you in your development tasks ! 


Quick start:
------------
java -Xmx512m -jar tattletale.jar [-exclude=<excludes>] <scan-directory> [output-directory]

Output: Analysis reports generate in current directory if no output-directory set.


User guide:
-----------
The JBoss Tattletale user guide is located in JBossTattletale-UsersGuide.pdf.


Developer guide:
----------------
The JBoss Tattletale developer guide is located in JBossTattletale-DevelopersGuide.pdf.


Development:
------------
Home          : http://www.jboss.org/projects/tattletale
Download      : http://www.jboss.org/projects/tattletale
Forum         : http://www.jboss.org/index.html?module=bb&op=viewforum&f=306
Issue tracking: http://jira.jboss.com/jira/browse/TTALE
AnonSVN       : http://anonsvn.jboss.org/repos/tattletale
Developer SVN : https://svn.jboss.org/repos/tattletale
