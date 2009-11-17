@echo off
mvn deploy:deploy-file -Dfile=../dist/jboss-tattletale.jar -DpomFile=core/snapshot.xml -Durl=dav:https://snapshots.jboss.org/maven2 -DrepositoryId=snapshots.jboss.org
