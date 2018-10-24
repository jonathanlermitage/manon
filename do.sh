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
  echo  "jib:    build Docker image to a Docker daemon"
  echo  "jibtar: build and save Docker image to a tarball"
  ;;

"t")
  echo "sh ./mvnw clean test"
  sh ./mvnw clean test -Pembed-linux
  ;;

"tc")
  echo "sh ./mvnw clean test -Pcoverage"
  sh ./mvnw clean test -Pembed-linux,coverage
  ;;

"b")
  echo "sh ./mvnw clean compile -DskipTests -T1"
  sh ./mvnw clean compile -DskipTests -T1
  ;;

"c")
  echo "sh ./mvnw clean"
  sh ./mvnw clean
  ;;

"p")
  echo "sh ./mvnw clean package -DskipTests -T1"
  sh ./mvnw clean package -DskipTests -T1
  ;;

"rd")
  echo "build project: sh ./mvnw clean package -DskipTests -T1"
  sh ./mvnw clean package -DskipTests -T1
  echo "move to app: cd target/"
  cd target/
  echo "run app in dev mode: java -jar -Xms128m -Xmx512m -Dspring.profiles.active=dev,metrics -Dfile.encoding=UTF-8 -Djava.awt.headless=true -XX:CompileThreshold=1500 manon.jar"
  java -jar -Xms128m -Xmx512m -Dspring.profiles.active=dev,metrics -Dfile.encoding=UTF-8 -Djava.awt.headless=true -XX:CompileThreshold=1500 manon.jar
  echo "application exit"
  echo "return to root: cd .."
  cd ..
  ;;

"w")
  echo "mvn -N io.takari:maven:wrapper -Dmaven=\$2"
  mvn -N io.takari:maven:wrapper -Dmaven=$2
  ;;

"cv")
  echo "sh ./mvnw versions:display-property-updates -U -P coverage,jib"
  sh ./mvnw versions:display-property-updates -U -P coverage,jib,embed-linux
  ;;

"uv")
  echo "sh ./mvnw versions:update-properties -U -P coverage,jib"
  sh ./mvnw versions:update-properties -U -P coverage,jib,embed-linux
  ;;

"dt")
  echo "sh ./mvnw dependency:tree"
  sh ./mvnw dependency:tree
  ;;

"sc")
  echo "sh ./mvnw sonar:sonar -Dsonar.organization=\$TK1_MANON_SONAR_ORGA -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=\$TK1_MANON_SONAR_LOGIN"
  sh ./mvnw sonar:sonar -Dsonar.organization=$TK1_MANON_SONAR_ORGA -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$TK1_MANON_SONAR_LOGIN
  ;;

"tsc")
  echo "sh ./mvnw clean test sonar:sonar -Pcoverage -Dsonar.organization=\$TK1_MANON_SONAR_ORGA -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=\$TK1_MANON_SONAR_LOGIN"
  sh ./mvnw clean test sonar:sonar -Pembed-linux,coverage -Dsonar.organization=$TK1_MANON_SONAR_ORGA -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$TK1_MANON_SONAR_LOGIN
  ;;

"rmi")
  echo "docker-compose stop"
  docker-compose stop
  echo "rm \$(docker ps -a | grep \"lermitage-manon\" | awk '{print \$1}')"
  docker rm $(docker ps -a | grep "lermitage-manon" | awk '{print $1}')
  echo "docker rmi \$(docker images | grep \"^lermitage-manon\" | awk '{print \$3}')"
  docker rmi $(docker images | grep "^lermitage-manon" | awk '{print $3}')
  ;;

"cdi")
  echo "rmi \$(docker images -f \"dangling=true\" -q)"
  docker rmi $(docker images -f "dangling=true" -q)
  ;;

"jib")
  echo "sh ./mvnw clean compile jib:dockerBuild -DskipTests -P jib"
  sh ./mvnw clean compile jib:dockerBuild -DskipTests -P jib
  ;;

"jibtar")
  echo "sh ./mvnw clean compile jib:buildTar -DskipTests -P jib"
  sh ./mvnw clean compile jib:buildTar -DskipTests -P jib
  ;;

esac
