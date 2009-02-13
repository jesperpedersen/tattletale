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

JBoss Tattletale will recursive scan the directory pass as the argument for JAR files and then
build the reports as HTML files.

The main HTML file is: index.html

JBoss Tattletale is licensed under GNU Lesser General Public License (LGPL) version 2.1 or later.

We hope that JBoss Tattletale will help you in your development tasks ! 


Running the client:
-------------------
java -Xmx512m -jar jboss-tattletale.jar <directory>

Output: Analysis reports in current working directory


Development:
------------
Home          : http://www.jboss.org/projects/tattletale
Download      : http://www.jboss.org/projects/tattletale
Forum         : http://www.jboss.org/index.html?module=bb&op=viewforum&f=306
Issue tracking: http://jira.jboss.com/jira/browse/TTALE
AnonSVN       : http://anonsvn.jboss.org/repos/tattletale
Developer SVN : https://svn.jboss.org/repos/tattletale
