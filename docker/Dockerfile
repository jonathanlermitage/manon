# package app with openjdk jre (see https://hub.docker.com/_/openjdk/)
FROM eclipse-temurin:17.0.5_8-jre

WORKDIR /home

COPY target/manon.jar ./app.jar

# set jdk.io.File.enableADS as a workaround for https://www.oracle.com/java/technologies/javase/11-0-15-1-relnotes.html#Remaining
ENTRYPOINT ["java","-Dfile.encoding=UTF-8","-Djdk.io.File.enableADS=true","-jar","/home/app.jar"]
