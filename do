#!/bin/bash

TitleColor=$(tput setaf 7)
CmdColor=$(tput setaf 4; tput bold)
OkColor=$(tput setaf 2; tput bold)
ResetColor=$(tput sgr0)

((nextParam = 1))
for ((cmd = 1; cmd <= $#; cmd++)); do

    ((nextParam++))

    case "${!cmd}" in

    "help")
        echo ""
        echo "${TitleColor}Helper: (tip: you can chain parameters, e.g.: \"./do cdi rmi docker\" or \"./do w 3.6.0 c t\")${ResetColor}"
        echo ""
        echo "${CmdColor}fixgit       ${ResetColor}set executable flag on git index for required files"
        echo "${CmdColor}fixexec      ${ResetColor}set executable permission for required files (chmod +x on bash scripts)"
        echo "${CmdColor}normgit      ${ResetColor}call git add --update --renormalize"
        echo "${CmdColor}conv         ${ResetColor}generate a Dependency Convergence report in target/site/dependency-convergence.html"
        echo "${CmdColor}oga          ${ResetColor}check for deprecated groupId and artifactId couples"
        echo "${CmdColor}owasp        ${ResetColor}generate a OWASP dependencies vulnerabilities report in target/dependency-check-report.html"
        echo "${CmdColor}t            ${ResetColor}test using embedded HSQLDB"
        echo "${CmdColor}td           ${ResetColor}test using dockerized MariaDB and Redis (container is started and stopped by script)"
        echo "${CmdColor}td-postgres  ${ResetColor}test using dockerized PostgreSQL and Redis (container is started and stopped by script)"
        echo "${CmdColor}ut           ${ResetColor}run unit tests only, no integration tests"
        echo "${CmdColor}tc           ${ResetColor}run unit + integration tests and generate coverage data"
        echo "${CmdColor}itc          ${ResetColor}run integration tests only and generate coverage data"
        echo "${CmdColor}pit          ${ResetColor}run mutation tests with Pitest"
        echo "${CmdColor}gatling      ${ResetColor}benchmark application via a Gatling container (run './do up' first to start application)"
        echo "${CmdColor}sc           ${ResetColor}compute and upload Sonar analysis to SonarCloud (set TK1_MANON_SONAR_ORGA and TK1_MANON_SONAR_LOGIN environment variables first)"
        echo "${CmdColor}tsc          ${ResetColor}similar to \"do tc\" then \"do sc\""
        echo "${CmdColor}sb           ${ResetColor}scan with SpotBugs then show GUI"
        echo "${CmdColor}b            ${ResetColor}compile"
        echo "${CmdColor}c            ${ResetColor}clean"
        echo "${CmdColor}p            ${ResetColor}package"
        echo "${CmdColor}rd           ${ResetColor}package and run application with dev-mariadb profile"
        echo "${CmdColor}w \$V         ${ResetColor}set or upgrade Maven wrapper to version \$V"
        echo "${CmdColor}cv           ${ResetColor}check plugins and dependencies versions"
        echo "${CmdColor}uv           ${ResetColor}update plugins and dependencies versions"
        echo "${CmdColor}dt           ${ResetColor}show dependencies tree"
        echo "${CmdColor}rmi          ${ResetColor}stop Docker application, then remove its containers and images"
        echo "${CmdColor}cdi          ${ResetColor}clean up dangling Docker images"
        echo "${CmdColor}dockerreset  ${ResetColor}stop and remove all containers, remove all images and prune volumes"
        echo "${CmdColor}docker       ${ResetColor}build Docker image with Dockerfile to a Docker daemon as lermitage-manon:1.0.0-SNAPSHOT"
        echo "${CmdColor}dockerpull   ${ResetColor}pull 3rd party Docker containers"
        echo "${CmdColor}jib          ${ResetColor}build Docker image with Jib to a Docker daemon as lermitage-manon:1.0.0-SNAPSHOT"
        echo "${CmdColor}jibtar       ${ResetColor}build and save Docker image with Jib to a tarball"
        echo "${CmdColor}up           ${ResetColor}create and start containers via docker-compose"
        echo "${CmdColor}stop         ${ResetColor}stop containers via docker-compose"
        echo "${CmdColor}upelk        ${ResetColor}create and start ELK containers via docker-compose"
        echo "${CmdColor}stopelk      ${ResetColor}stop ELK containers via docker-compose"
        echo "${CmdColor}upcerebro    ${ResetColor}create and start Cerebro container via docker-compose"
        echo "${CmdColor}stopcerebro  ${ResetColor}stop Cerebro container via docker-compose"
        echo "${CmdColor}maria        ${ResetColor}connect to dockerized MariaDB business database by calling MySQL Client provided by container"
        echo "${CmdColor}maria-batch  ${ResetColor}connect to dockerized MariaDB Spring Batch database by calling MySQL Client provided by container"
        echo "${CmdColor}mariah       ${ResetColor}connect to dockerized MariaDB business database by calling host MySQL Client (mysql-client package must be installed)"
        echo "${CmdColor}mariah-batch ${ResetColor}connect to dockerized MariaDB Spring Batch database by calling host MySQL Client (mysql-client package must be installed)"
        echo "${CmdColor}e2e          ${ResetColor}run some end-to-end (e2e) tests with Docker. Application image is built from a Dockerfile"
        echo "${CmdColor}e2ejib       ${ResetColor}run some end-to-end (e2e) tests with Docker. Application image is built with Jib"
        ;;

    "fixgit")
        git update-index --chmod=+x "do"
        echo "${OkColor}'do' has now executable flag on git index${ResetColor}"
        git update-index --chmod=+x "mvnw"
        echo "${OkColor}'mvnw' has now executable flag on git index${ResetColor}"
        git update-index --chmod=+x "e2e/run-e2e-docker.sh"
        echo "${OkColor}'e2e/run-e2e-docker.sh' has now executable flag on git index${ResetColor}"
        git update-index --chmod=+x "e2e/run-e2e-jib.sh"
        echo "${OkColor}'e2e/run-e2e-jib.sh' has now executable flag on git index${ResetColor}"
        git update-index --chmod=+x "e2e/_e2e-executor.sh"
        echo "${OkColor}'e2e/_e2e-executor.sh' has now executable flag on git index${ResetColor}"
        ;;

    "fixexec")
        chmod +x ./do
        echo "${OkColor}'do' is now executable${ResetColor}"
        chmod +x ./mvnw
        echo "${OkColor}'mvnw' is now executable${ResetColor}"
        chmod +x ./e2e/run-e2e-docker.sh
        echo "${OkColor}'e2e/run-e2e-docker.sh' is now executable${ResetColor}"
        chmod +x ./e2e/run-e2e-jib.sh
        echo "${OkColor}'e2e/run-e2e-jib.sh' is now executable${ResetColor}"
        chmod +x ./e2e/_e2e-executor.sh
        echo "${OkColor}'e2e/_e2e-executor.sh' is now executable${ResetColor}"
        ;;

    "normgit")
        git add --update --renormalize
        echo "${OkColor}renormalized${ResetColor}"
        ;;

    "conv")
        sh ./mvnw project-info-reports:dependency-convergence -U
        ;;

    "oga")
        sh ./mvnw biz.lermitage.oga:oga-maven-plugin:check
        ;;

    "owasp")
        sh ./mvnw org.owasp:dependency-check-maven:check -P owasp
        ;;

    "t")
        sh ./mvnw verify -U
        ;;

    "td")
        echo "remove test containers"
        docker-compose -f ./docker/docker-compose-test.yml down
        echo "start test containers"
        docker-compose -f ./docker/docker-compose-test.yml up -d maria-test
        docker-compose -f ./docker/docker-compose-test.yml up -d maria-batch-test
        docker-compose -f ./docker/docker-compose-test.yml up -d redis-test
        echo "run tests"
        sh ./mvnw verify -P test-mariadb \
            -DMANON_TEST_SQL_JDBC_URL="jdbc:mariadb://127.0.0.1:3307/manon?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useMysqlMetadata=true" \
            -DMANON_TEST_BATCH_SQL_JDBC_URL="jdbc:mariadb://127.0.0.1:3308/manon_batch?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useMysqlMetadata=true" \
            -DMANON_TEST_REDIS_PORT=6380
        echo "stop test containers"
        docker-compose -f ./docker/docker-compose-test.yml down
        ;;

    "td-postgres")
        echo "remove test containers"
        docker-compose -f ./docker/docker-compose-test.yml down
        echo "start test containers"
        docker-compose -f ./docker/docker-compose-test.yml up -d postgres-test
        docker-compose -f ./docker/docker-compose-test.yml up -d postgres-batch-test
        docker-compose -f ./docker/docker-compose-test.yml up -d redis-test
        echo "run tests"
        sh ./mvnw verify -P test-postgres \
            -DMANON_TEST_SQL_JDBC_URL="jdbc:postgresql://127.0.0.1:5440/manon" \
            -DMANON_TEST_BATCH_SQL_JDBC_URL="jdbc:postgresql://127.0.0.1:5441/manon_batch" \
            -DMANON_TEST_REDIS_PORT=6380
        echo "stop test containers"
        docker-compose -f ./docker/docker-compose-test.yml down
        ;;

    "ut")
        sh ./mvnw test -U
        ;;

    "tc")
        sh ./mvnw verify -U -P coverage
        ;;

    "itc")
        sh ./mvnw verify -U -P coverage -DskipUT=true
        ;;

    "pit")
        sh ./mvnw clean compile test-compile -DwithHistory org.pitest:pitest-maven:mutationCoverage
        ;;

    "b")
        sh ./mvnw compile -DskipUT=true -DskipIT=true -T1 -U
        ;;

    "c")
        sh ./mvnw clean
        ;;

    "p")
        sh ./mvnw package -DskipUT=true -DskipIT=true -T1 -U
        ;;

    "rd")
        sh ./mvnw package -DskipUT=true -DskipIT=true -T1 -U
        (
            cd target/ || exit
            java -jar -Xms128m -Xmx512m -Dspring.profiles.active=dev-mariadb -Dfile.encoding=UTF-8 -Djdk.io.File.enableADS=true -Djava.awt.headless=true -XX:CompileThreshold=1500 manon.jar
        )
        ;;

    "w")
        mvn -N io.takari:maven:wrapper -Dmaven="${!nextParam}"
        ;;

    "cv")
        sh ./mvnw versions:display-property-updates -U
        ;;

    "uv")
        sh ./mvnw versions:update-properties -U
        ;;

    "dt")
        sh ./mvnw dependency:tree -U
        ;;

    "sc")
        sh ./mvnw sonar:sonar -Dsonar.organization="$TK1_MANON_SONAR_ORGA" -Dsonar.host.url=https://sonarcloud.io -Dsonar.login="$TK1_MANON_SONAR_LOGIN"
        ;;

    "tsc")
        sh ./mvnw verify sonar:sonar -U -P coverage -Dsonar.organization="$TK1_MANON_SONAR_ORGA" -Dsonar.host.url=https://sonarcloud.io -Dsonar.login="$TK1_MANON_SONAR_LOGIN"
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
        if [[ -d ~/manon-grafana-data ]]; then
            sudo rm -R ~/manon-grafana-data
        fi
        ;;

    "docker")
        docker rm $(docker ps -a | grep "lermitage-manon" | awk '{print $1}')
        docker rmi $(docker images | grep -E "^lermitage-manon|<none>" | awk '{print $3}')
        sh ./mvnw package -DskipUT=true -DskipIT=true -T1
        docker build -f ./docker/Dockerfile -t lermitage-manon:1.0.0-SNAPSHOT .
        ;;

    "dockerpull")
        docker-compose -f ./docker/docker-compose.yml pull
        docker-compose -f ./docker/docker-compose-cerebro.yml pull
        docker-compose -f ./docker/docker-compose-elk.yml pull
        docker-compose -f ./docker/docker-compose-gatling.yml pull
        docker-compose -f ./docker/docker-compose-test.yml pull
        ;;

    "jib")
        docker rm $(docker ps -a | grep "lermitage-manon" | awk '{print $1}')
        docker rmi $(docker images | grep -E "^lermitage-manon|<none>" | awk '{print $3}')
        sh ./mvnw compile jib:dockerBuild -DskipUT=true -DskipIT=true -U -P jib
        ;;

    "jibtar")
        sh ./mvnw compile jib:buildTar -DskipUT=true -DskipIT=true -U -P jib
        ;;

    "up")
        if [[ ! -d ~/manon-app-logs ]]; then
            mkdir ~/manon-app-logs
            echo "$HOME/manon-app-logs directory created"
        fi
        if [[ ! -d ~/manon-maria-db ]]; then
            mkdir ~/manon-maria-db
            docker-compose -f ./docker/docker-compose.yml up -d maria
            echo "$HOME/manon-maria-db directory created, starting maria and wait 15 seconds..."
            sleep 15
            echo "Done, maria should be open to connections. If manon startup fails, please restart maria"
        fi
        if [[ ! -d ~/manon-maria-db-batch ]]; then
            mkdir ~/manon-maria-db-batch
            docker-compose -f ./docker/docker-compose.yml up -d maria-batch
            echo "$HOME/manon-maria-db-batch directory created, starting maria and wait 15 seconds..."
            sleep 15
            echo "Done, maria-batch should be open to connections. If manon startup fails, please restart maria-batch"
        fi
        if [[ ! -d ~/manon-nginx-logs ]]; then
            mkdir ~/manon-nginx-logs
            echo "$HOME/manon-nginx-logs directory created"
        fi
        if [[ ! -d ~/manon-grafana-data ]]; then
            if ! getent passwd grafana >/dev/null 2>&1; then
                sudo groupadd grafana -g 472
                sudo useradd grafana -u 472 -g grafana
            fi
            mkdir ~/manon-grafana-data
            sudo chown -R grafana ~/manon-grafana-data
            echo "$HOME/manon-grafana-data directory created"
        fi
        docker-compose -f ./docker/docker-compose.yml up -d
        ;;

    "stop")
        docker-compose -f ./docker/docker-compose.yml down
        ;;

    "upelk")
        if [[ ! -d ~/manon-elastic-db ]]; then
            mkdir ~/manon-elastic-db
            echo "$HOME/manon-elastic-db directory created"
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

    "gatling")
        docker-compose -f ./docker/docker-compose-gatling.yml up --build --force-recreate --renew-anon-volumes
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

    "e2e")
        ./e2e/run-e2e-docker.sh
        ;;

    "e2ejib")
        ./e2e/run-e2e-jib.sh
        ;;

    esac

done
