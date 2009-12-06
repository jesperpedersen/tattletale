#!/bin/sh
mvn deploy:deploy-file -Dfile=../dist/tattletale.jar -DpomFile=core/release.xml -Durl=dav:https://svn.jboss.org/repos/repository.jboss.org/maven2 -DrepositoryId=jboss-releases
mvn deploy:deploy-file -Dfile=../dist/tattletale-ant.jar -DpomFile=ant/release.xml -Durl=dav:https://svn.jboss.org/repos/repository.jboss.org/maven2 -DrepositoryId=jboss-releases
mvn deploy:deploy-file -Dfile=../dist/tattletale-maven.jar -DpomFile=maven/release.xml -Durl=dav:https://svn.jboss.org/repos/repository.jboss.org/maven2 -DrepositoryId=jboss-releases
