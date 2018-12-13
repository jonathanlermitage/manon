#!/bin/bash

let "nextParam = 1"
for ((cmd = 1; cmd <= $#; cmd++)) do

    (( nextParam++ ))

    case "${!cmd}" in

    "help")
      echo ""
      echo  "Helper: (tip: you can chain parameters, e.g.: \"./do.sh cdi rmi docker\" or \"./do.sh w 3.6.0 c t\")"
      echo  ""
      echo  "t            test"
      echo  "tc           test and generate coverage data"
      echo  "sc           compute and upload Sonar analysis to SonarCloud (set TK1_MANON_SONAR_ORGA and TK1_MANON_SONAR_LOGIN environment variables first)"
      echo  "tsc          similar to \"do tc\" then \"do sc\""
      echo  "b            compile"
      echo  "c            clean"
      echo  "p            package"
      echo  "rd           package and run application with dev profile"
      echo  "w \$V         set or upgrade Maven wrapper to version \$V"
      echo  "cv           check plugins and dependencies versions"
      echo  "uv           update plugins and dependencies versions"
      echo  "dt           show dependencies tree"
      echo  "rmi          stop Docker application, then remove its containers and images"
      echo  "cdi          clean up dangling Docker images"
      echo  "docker       build Docker image with Dockerfile to a Docker daemon as lermitage-manon:1.0.0-SNAPSHOT"
      echo  "jib          build Docker image with Jib to a Docker daemon as lermitage-manon:1.0.0-SNAPSHOT"
      echo  "jibtar       build and save Docker image with Jib to a tarball"
      echo  "up           create and start containers via docker-compose"
      echo  "stop         stop containers via docker-compose"
      echo  "upelk        create and start ELK containers via docker-compose"
      echo  "stopelk      stop ELK containers via docker-compose"
      echo  "upcerebro    create and start Cerebro container via docker-compose"
      echo  "stopcerebro  stop Cerebro container via docker-compose"
      ;;

    "t")
      sh ./mvnw test -Pembed-linux
      ;;

    "tc")
      sh ./mvnw test -Pembed-linux,coverage
      ;;

    "b")
      sh ./mvnw compile -DskipTests -T1
      ;;

    "c")
      sh ./mvnw clean
      ;;

    "p")
      sh ./mvnw package -DskipTests -T1
      ;;

    "rd")
      sh ./mvnw package -DskipTests -T1
      cd target/
      java -jar -Xms128m -Xmx512m -Dspring.profiles.active=dev,metrics -Dfile.encoding=UTF-8 -Djava.awt.headless=true -XX:CompileThreshold=1500 manon.jar
      cd ..
      ;;

    "w")
      mvn -N io.takari:maven:wrapper -Dmaven=${!nextParam}
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
      sh ./mvnw test sonar:sonar -Pembed-linux,coverage -Dsonar.organization=$TK1_MANON_SONAR_ORGA -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$TK1_MANON_SONAR_LOGIN
      ;;

    "rmi")
      docker-compose -f ./config/docker/docker-compose.yml stop
      docker rm $(docker ps -a | grep "lermitage-manon" | awk '{print $1}')
      docker rmi $(docker images | grep -E "^lermitage-manon|gcr.io/distroless/java|<none>" | awk '{print $3}')
      ;;

    "cdi")
      docker rmi $(docker images -f "dangling=true" -q)
      ;;

    "docker")
      sh ./mvnw package -DskipTests -T1
      docker build -t lermitage-manon:1.0.0-SNAPSHOT .
      ;;

    "jib")
      sh ./mvnw compile jib:dockerBuild -DskipTests -P jib
      ;;

    "jibtar")
      sh ./mvnw compile jib:buildTar -DskipTests -P jib
      ;;

    "up")
      docker-compose -f ./config/docker/docker-compose.yml up -d
      ;;

    "stop")
      docker-compose -f ./config/docker/docker-compose.yml stop
      ;;

    "upelk")
      docker-compose -f ./config/docker/docker-compose-elk.yml up -d
      ;;

    "stopelk")
      docker-compose -f ./config/docker/docker-compose-elk.yml stop
      ;;

    "upcerebro")
      docker-compose -f ./config/docker/docker-compose-cerebro.yml up -d
      ;;

    "stopcerebro")
      docker-compose -f ./config/docker/docker-compose-cerebro.yml stop
      ;;

    esac

done
