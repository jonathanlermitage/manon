FROM gcr.io/distroless/java@sha256:84740ded6cbeebaa89c9b19aed3754d464586592b561e5494be4192ac0b3a8f5
VOLUME /tmp
COPY target/manon.jar app.jar
ENTRYPOINT ["java","-Dfile.encoding=UTF-8","-Djava.awt.headless=true","-XX:+UnlockExperimentalVMOptions","-XX:+UseCGroupMemoryLimitForHeap","-Dserver.port=8080","-Dspring.jmx.enabled=false","-Dspring.profiles.active=docker,metrics","-jar","/app.jar"]
