# package app with openjdk jre (see https://hub.docker.com/_/openjdk/)
FROM openjdk:11.0.2-jre-slim
VOLUME /tmp
COPY target/manon.jar app.jar
ENTRYPOINT ["java","-Dfile.encoding=UTF-8","-Dspring.jmx.enabled=false","-Dspring.profiles.active=docker","-jar","/app.jar"]
