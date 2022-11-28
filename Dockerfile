FROM openjdk:17
COPY target/task-manager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 7002
ENTRYPOINT ["java", "-jar", "/app.jar", "-web -webAllowOthers -tcp -tcpAllowOthers -browser"]