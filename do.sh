#!/bin/bash

case "$1" in

"help")
  echo  "t:      test"
  echo  "tc:     test and generate coverage data"
  echo  "sc:     compute and upload Sonar analysis to SonarCloud"
  echo  "tsc:    similar to \"do tc\" then \"do sc\""
  echo  "b:      compile"
  echo  "c:      clean"
  echo  "p:      package"
  echo  "rd:     package and run application with dev profile"
  echo  "w \$V:   set or upgrade Maven wrapper to version \$V"
  echo  "cv:     check plugins and dependencies versions"
  echo  "uv:     update plugins and dependencies versions"
  echo  "dt:     show dependencies tree"
  echo  "rmi:    stop Docker application, then remove its containers and images"
  echo  "cdi:    clean up dangling Docker images"
  echo  "docker  build Docker image with Dockerfile to a Docker daemon as lermitage-manon:1.0.0-SNAPSHOT"
  echo  "jib:    build Docker image with Jib to a Docker daemon as lermitage-manon:1.0.0-SNAPSHOT"
  echo  "jibtar: build and save Docker image with Jib to a tarball"
  ;;

"t")
  sh ./mvnw clean test -Pembed-linux
  ;;

"tc")
  sh ./mvnw clean test -Pembed-linux,coverage
  ;;

"b")
  sh ./mvnw clean compile -DskipTests -T1
  ;;

"c")
  sh ./mvnw clean
  ;;

"p")
  sh ./mvnw clean package -DskipTests -T1
  ;;

"rd")
  sh ./mvnw clean package -DskipTests -T1
  cd target/
  java -jar -Xms128m -Xmx512m -Dspring.profiles.active=dev,metrics -Dfile.encoding=UTF-8 -Djava.awt.headless=true -XX:CompileThreshold=1500 manon.jar
  cd ..
  ;;

"w")
  mvn -N io.takari:maven:wrapper -Dmaven=$2
  ;;

"cv")
  sh ./mvnw versions:display-property-updates -U -P coverage,jib,embed-linux
  ;;

"uv")
  sh ./mvnw versions:update-properties -U -P coverage,jib,embed-linux
  ;;

"dt")
  sh ./mvnw dependency:tree
  ;;

"sc")
  sh ./mvnw sonar:sonar -Dsonar.organization=$TK1_MANON_SONAR_ORGA -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$TK1_MANON_SONAR_LOGIN
  ;;

"tsc")
  sh ./mvnw clean test sonar:sonar -Pembed-linux,coverage -Dsonar.organization=$TK1_MANON_SONAR_ORGA -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$TK1_MANON_SONAR_LOGIN
  ;;

"rmi")
  docker-compose stop
  docker rm $(docker ps -a | grep "lermitage-manon" | awk '{print $1}')
  docker rmi $(docker images | grep -E "^lermitage-manon|gcr.io/distroless/java|<none>" | awk '{print $3}')
  ;;

"cdi")
  docker rmi $(docker images -f "dangling=true" -q)
  ;;

"docker")
  sh ./mvnw clean package -DskipTests -T1
  docker build -t lermitage-manon:1.0.0-SNAPSHOT .
  ;;

"jib")
  sh ./mvnw clean compile jib:dockerBuild -DskipTests -P jib
  ;;

"jibtar")
  sh ./mvnw clean compile jib:buildTar -DskipTests -P jib
  ;;

esac
