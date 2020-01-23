# package app with openjdk jre (see https://hub.docker.com/_/openjdk/)
FROM openjdk:11.0.6-jre-slim

RUN apt-get update && apt-get install -y --no-install-recommends curl unzip && rm -rf /var/lib/apt/lists/*

## install Glowroot
# - download and copy Glowroot distribution
RUN curl -L https://github.com/glowroot/glowroot/releases/download/v0.13.5/glowroot-0.13.5-dist.zip > glowroot-dist.zip \
  && unzip glowroot-dist.zip \
  && rm glowroot-dist.zip \
  && apt-get -y purge --auto-remove unzip \
  && rm -rf /var/lib/apt/lists/* \
  && chmod -R 777 /glowroot/
# - use custom Glowroot config to listen on 0.0.0.0 instead of localhost only
COPY docker/glowroot/admin.json glowroot/admin.json

VOLUME /tmp

COPY target/manon.jar app.jar

ENTRYPOINT ["java","-Dfile.encoding=UTF-8","-javaagent:/glowroot/glowroot.jar","-jar","/app.jar"]
