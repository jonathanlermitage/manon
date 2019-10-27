#!/bin/bash

let "nextParam = 1"
for ((cmd = 1; cmd <= $#; cmd++)) do

    (( nextParam++ ))

    case "${!cmd}" in

    "help")
      echo ""
      echo  "Helper: (tip: you can chain parameters, e.g.: \"./do cdi rmi docker\" or \"./do w 3.6.0 c t\")"
      echo  ""
      echo  "fixgit       set executable flag on git index for required files"
      echo  "oga          check for deprecated groupId and artifactId couples"
      echo  "t            test using embedded HSQLDB"
      echo  "td           test using dockerized MariaDB and Redis (container is started and stopped by script)"
      echo  "ut           run unit tests only, no integration tests"
      echo  "tc           run unit + integration tests and generate coverage data"
      echo  "itc          run integration tests only and generate coverage data"
      echo  "sc           compute and upload Sonar analysis to SonarCloud (set TK1_MANON_SONAR_ORGA and TK1_MANON_SONAR_LOGIN environment variables first)"
      echo  "tsc          similar to \"do tc\" then \"do sc\""
      echo  "sb           scan with SpotBugs then show GUI"
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
      echo  "dockerreset  stop and remove all containers, remove all images and prune volumes"
      echo  "docker       build Docker image with Dockerfile to a Docker daemon as lermitage-manon:1.0.0-SNAPSHOT"
      echo  "jib          build Docker image with Jib to a Docker daemon as lermitage-manon:1.0.0-SNAPSHOT"
      echo  "jibtar       build and save Docker image with Jib to a tarball"
      echo  "up           create and start containers via docker-compose"
      echo  "stop         stop containers via docker-compose"
      echo  "upelk        create and start ELK containers via docker-compose"
      echo  "stopelk      stop ELK containers via docker-compose"
      echo  "upcerebro    create and start Cerebro container via docker-compose"
      echo  "stopcerebro  stop Cerebro container via docker-compose"
      echo  "maria        connect to dockerized MariaDB business database by calling MySQL Client provided by container"
      echo  "maria-batch  connect to dockerized MariaDB Spring Batch database by calling MySQL Client provided by container"
      echo  "mariah       connect to dockerized MariaDB business database by calling host MySQL Client (mysql-client package must be installed)"
      echo  "mariah-batch connect to dockerized MariaDB Spring Batch database by calling host MySQL Client (mysql-client package must be installed)"
      ;;

    "fixgit")
      git update-index --chmod=+x do
      echo "'do' has now executable flag on git index"
      git update-index --chmod=+x mvnw
      echo "'mvnw' has now executable flag on git index"
      ;;

    "oga")
      sh ./mvnw biz.lermitage.oga:oga-maven-plugin:check
      ;;

    "t")
      sh ./mvnw verify
      ;;

    "td")
      echo "remove test containers"
      docker-compose -f ./docker/docker-compose-test.yml down
      echo "start test containers"
      docker-compose -f ./docker/docker-compose-test.yml up -d
      set PREV__MANON_TEST_SQL_JDBC_URL=$MANON_TEST_SQL_JDBC_URL
      set PREV__MANON_TEST_BATCH_SQL_JDBC_URL=$MANON_TEST_BATCH_SQL_JDBC_URL
      set PREV__MANON_TEST_REDIS_PORT=$MANON_TEST_REDIS_PORT
      export MANON_TEST_SQL_JDBC_URL="jdbc:mariadb://127.0.0.1:3307/manon?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useMysqlMetadata=true"
      export MANON_TEST_BATCH_SQL_JDBC_URL="jdbc:mariadb://127.0.0.1:3308/manon_batch?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useMysqlMetadata=true"
      export MANON_TEST_REDIS_PORT=6380
      echo "run tests"
      sh ./mvnw verify -P test-real
      export MANON_TEST_SQL_JDBC_URL=PREV__MANON_TEST_SQL_JDBC_URL
      export MANON_TEST_BATCH_SQL_JDBC_URL=PREV__MANON_TEST_BATCH_SQL_JDBC_URL
      export MANON_TEST_REDIS_PORT=PREV__MANON_TEST_REDIS_PORT
      echo "stop test containers"
      docker-compose -f ./docker/docker-compose-test.yml down
      ;;

    "ut")
      sh ./mvnw test
      ;;

    "tc")
      sh ./mvnw verify -P coverage
      ;;

    "itc")
      sh ./mvnw verify -P coverage-it
      ;;

    "b")
      sh ./mvnw compile -DskipUT=true -DskipIT=true -T1
      ;;

    "c")
      sh ./mvnw clean
      ;;

    "p")
      sh ./mvnw package -DskipUT=true -DskipIT=true -T1
      ;;

    "rd")
      sh ./mvnw package -DskipUT=true -DskipIT=true -T1
      cd target/
      java -jar -Xms128m -Xmx512m -Dspring.profiles.active=dev -Dfile.encoding=UTF-8 -Djava.awt.headless=true -XX:CompileThreshold=1500 manon.jar
      cd ..
      ;;

    "w")
      mvn -N io.takari:maven:wrapper -Dmaven=${!nextParam}
      ;;

    "cv")
      sh ./mvnw versions:display-property-updates -U
      ;;

    "uv")
      sh ./mvnw versions:update-properties -U
      ;;

    "dt")
      sh ./mvnw dependency:tree
      ;;

    "sc")
      sh ./mvnw sonar:sonar -Dsonar.organization=$TK1_MANON_SONAR_ORGA -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$TK1_MANON_SONAR_LOGIN
      ;;

    "tsc")
      sh ./mvnw verify sonar:sonar -P coverage -Dsonar.organization=$TK1_MANON_SONAR_ORGA -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$TK1_MANON_SONAR_LOGIN
      ;;

    "sb")
      sh ./mvnw clean compile spotbugs:spotbugs spotbugs:gui -P spotbugs
      ;;

    "rmi")
      docker-compose -f ./docker/docker-compose.yml stop
      docker rm $(docker ps -a | grep "lermitage-manon" | awk '{print $1}')
      docker rmi $(docker images | grep -E "^lermitage-manon|<none>" | awk '{print $3}')
      ;;

    "cdi")
      docker rmi $(docker images -f "dangling=true" -q)
      ;;

    "dockerreset")
      docker stop $(docker ps -a -q)
      docker rm $(docker ps -a -q)
      docker rmi $(docker images -a -q)
      docker volume prune --force
      if [[ -d ~/manon-app-logs ]]; then
        sudo rm -R ~/manon-app-logs
      fi
      if [[ -d ~/manon-maria-db ]]; then
        sudo rm -R ~/manon-maria-db
      fi
      if [[ -d ~/manon-maria-db-batch ]]; then
        sudo rm -R ~/manon-maria-db-batch
      fi
      if [[ -d ~/manon-nginx-logs ]]; then
        sudo rm -R ~/manon-nginx-logs
      fi
      if [[ -d ~/manon-elastic-db ]]; then
        sudo rm -R ~/manon-elastic-db
      fi
      ;;

    "docker")
      sh ./mvnw package -DskipUT=true -DskipIT=true -T1
      docker build -f ./docker/Dockerfile -t lermitage-manon:1.0.0-SNAPSHOT .
      ;;

    "jib")
      sh ./mvnw compile jib:dockerBuild -DskipUT=true -DskipIT=true -P jib
      ;;

    "jibtar")
      sh ./mvnw compile jib:buildTar -DskipUT=true -DskipIT=true -P jib
      ;;

    "up")
      if [[ ! -d ~/manon-app-logs ]]; then
        mkdir ~/manon-app-logs
        echo "~/manon-app-logs directory created"
      fi
      if [[ ! -d ~/manon-maria-db ]]; then
        mkdir ~/manon-maria-db
        docker-compose -f ./docker/docker-compose.yml up -d maria
        echo "~/manon-maria-db directory created, starting maria and wait 15 seconds..."
        sleep 15
        echo "Done, maria should be open to connections. If manon startup fails, please restart maria"
      fi
      if [[ ! -d ~/manon-maria-db-batch ]]; then
        mkdir ~/manon-maria-db-batch
        docker-compose -f ./docker/docker-compose.yml up -d maria-batch
        echo "~/manon-maria-db-batch directory created, starting maria and wait 15 seconds..."
        sleep 15
        echo "Done, maria-batch should be open to connections. If manon startup fails, please restart maria-batch"
      fi
      if [[ ! -d ~/manon-nginx-logs ]]; then
        mkdir ~/manon-nginx-logs
        echo "~/manon-nginx-logs directory created"
      fi
      docker-compose -f ./docker/docker-compose.yml up -d
      ;;

    "stop")
      docker-compose -f ./docker/docker-compose.yml down
      ;;

    "upelk")
      if [[ ! -d ~/manon-elastic-db ]]; then
        mkdir ~/manon-elastic-db
        echo "~/manon-elastic-db directory created"
      fi
      docker-compose -f ./docker/docker-compose-elk.yml up -d
      ;;

    "stopelk")
      docker-compose -f ./docker/docker-compose-elk.yml down
      ;;

    "upcerebro")
      docker-compose -f ./docker/docker-compose-cerebro.yml up -d
      ;;

    "stopcerebro")
      docker-compose -f ./docker/docker-compose-cerebro.yml down
      ;;

    "maria")
      docker exec -it maria mysql --user=root --password=woot manon
      ;;

    "maria-batch")
      docker exec -it maria-batch mysql --port 3308 --user=root --password=woot manon_batch
      ;;

    "mariah")
      mysql -h $(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' maria) --port 3306 --protocol=TCP --user=root --password=woot manon
      ;;

    "mariah-batch")
      mysql -h $(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' maria-batch) --port 3306 --protocol=TCP --user=root --password=woot manon_batch
      ;;

    esac

done
