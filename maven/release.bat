@echo off
mvn deploy:deploy-file -Dfile=../dist/jboss-tattletale.jar -DpomFile=core/release.xml -Durl=dav:https://svn.jboss.org/repos/repository.jboss.org/maven2 -DrepositoryId=jboss-releases
