@echo off
mvn deploy:deploy-file -Dfile=../dist/tattletale.jar -DpomFile=core/snapshot.xml -Durl=dav:https://snapshots.jboss.org/maven2 -DrepositoryId=snapshots.jboss.org
mvn deploy:deploy-file -Dfile=../dist/tattletale-ant.jar -DpomFile=ant/snapshot.xml -Durl=dav:https://snapshots.jboss.org/maven2 -DrepositoryId=snapshots.jboss.org
mvn deploy:deploy-file -Dfile=../dist/tattletale-maven.jar -DpomFile=maven/snapshot.xml -Durl=dav:https://snapshots.jboss.org/maven2 -DrepositoryId=snapshots.jboss.org
