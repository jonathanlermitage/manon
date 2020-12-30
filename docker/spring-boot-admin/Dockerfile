# package app with openjdk jre (see https://hub.docker.com/_/openjdk/)
FROM openjdk:11.0.9.1-jre-slim

VOLUME /tmp

COPY target/spring-boot-admin.jar app.jar

ENTRYPOINT ["java","-Dfile.encoding=UTF-8","-jar","/app.jar"]
