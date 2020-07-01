<p align="center">
    <a href="https://dev.azure.com/jonathanlermitage/manon/_build?definitionId=1&view=buildsHistory"><img src="https://dev.azure.com/jonathanlermitage/manon/_apis/build/status/jonathanlermitage.manon?branchName=spring5-light" alt="Azure CI"/></a>
    <a href="https://ci.appveyor.com/project/jonathanlermitage/manon"><img src="https://ci.appveyor.com/api/projects/status/3tfcq04yte3ff1iq?svg=true" alt="Appveyor CI"/></a>
    <a href="https://travis-ci.org/jonathanlermitage/manon"><img src="https://img.shields.io/travis/jonathanlermitage/manon/spring5-light.svg?logo=travis" alt="Travis CI"/></a>
    <a href="http://cirrus-ci.com/github/jonathanlermitage/manon/spring5-light"><img src="https://api.cirrus-ci.com/github/jonathanlermitage/manon.svg?branch=spring5-light" alt="Cirrus CI"/></a>
    <a href="https://github.com/jonathanlermitage/manon/blob/master/LICENSE.txt"><img src="https://img.shields.io/github/license/jonathanlermitage/manon.svg" alt="License"/></a>
    <br/>
    <a href="https://sonarcloud.io/dashboard?id=nanon%3Amanon-light"><img src="https://sonarcloud.io/api/project_badges/measure?project=nanon%3Amanon-light&metric=alert_status" alt="SonarCloud"/></a>
    <a href="https://codecov.io/gh/jonathanlermitage/manon/branch/spring5-light"><img src="https://codecov.io/gh/jonathanlermitage/manon/branch/spring5-light/graph/badge.svg" alt="Codecov"/></a>
    <a href="https://lgtm.com/projects/g/jonathanlermitage/manon/alerts/"><img src="https://img.shields.io/lgtm/alerts/g/jonathanlermitage/manon.svg?logo=lgtm&logoWidth=18" alt="LGTM alerts"/></a>
    <a href="https://lgtm.com/projects/g/jonathanlermitage/manon/context:java"><img src="https://img.shields.io/lgtm/grade/java/g/jonathanlermitage/manon.svg?logo=lgtm&logoWidth=18" alt="LGTM grade"/></a>
    <a href="https://bettercodehub.com/results/jonathanlermitage/manon"><img src="https://bettercodehub.com/edge/badge/jonathanlermitage/manon?branch=spring5-light" alt="Bettercodehub"></a>
</p>

1. [Project](https://github.com/jonathanlermitage/manon#project)  
2. [Author](https://github.com/jonathanlermitage/manon#author)
3. [Compilation and test](https://github.com/jonathanlermitage/manon#compilation-and-test)
4. [License](https://github.com/jonathanlermitage/manon#license)

## Project

Some experimentation with **Spring Boot 2**, JDK8+, JUnit5, TestNG, SQL (HSQLDB, MariaDB, PostgreSQL), NoSQL (Redis, MongoDB), Docker, ELK stack, etc. It demonstrates usage of:

* **Maven** (latest version) and **Gradle** build tools
  * migration from **Maven to Gradle** (but I'll stick to Maven). Stable and functional, but still needs some improvements. See commit [cf79b9c](https://github.com/jonathanlermitage/manon/commit/cf79b9c1f0a7eee7ffcd8a1fd0b1e05e11f1de75) ([spring5-mvn-to-gradle](https://github.com/jonathanlermitage/manon/tree/spring5-mvn-to-gradle) branch)
* **Spring Boot 2** (Spring Framework 5)
  * you can compare [spring4](https://github.com/jonathanlermitage/manon/tree/spring4) and [spring5](https://github.com/jonathanlermitage/manon/tree/spring5) branches to study migration from Spring Boot 1 (Spring Framework 4) to Spring Boot 2
* built with JDK8, JDK11 and JDK13 on Travis CI, targets Java 8 bytecode
* replace **Tomcat** by **Undertow**. See last commit of [spring5-light-undertow](https://github.com/jonathanlermitage/manon/tree/spring5-light-undertow) branch. Also, it seems to fix some annoying `ConnectionClosedException: Premature end of chunk coded message body: closing chunk expected` issues when sending a large number (2000+) of HTTP requests with message body
* **Spring Web** with a **REST API**
* **Spring Security**, to authenticate users via auth_basic and fetch authentication data from SQL database 
* **Spring Data** to serve data from a database
  * simple test to show when a transaction rollback occurs (tip: Error and RuntimeException trigger rollback, Exception doesn't!). See last commit of [spring5-light-trx-rollback](https://github.com/jonathanlermitage/manon/tree/spring5-light-trx-rollback) branch. See also [`AbstractManagedException` javadoc](https://github.com/jonathanlermitage/manon/blob/spring5-light/src/main/java/manon/err/AbstractManagedException.java) for details.
* use [**MapStruct**](https://mapstruct.org) to map entity to DTO (this is a short introduction only, mapping can be done in both directions and extended to all entities). See last commit of [spring5-light-mapstruct-mapper](https://github.com/jonathanlermitage/manon/tree/spring5-light-mapstruct-mapper) branch
* [**Querydsl**](http://www.querydsl.com) support for dynamic SQL querying and pagination with Spring Data JPA and Spring Web. See last commit of [spring5-light-querydsl-jpa](https://github.com/jonathanlermitage/manon/tree/spring5-light-querydsl-jpa) branch
* **Spring Batch** to schedule and manage some tasks. See commit [c0e3422](https://github.com/jonathanlermitage/manon/commit/c0e3422fcce5522c3320dd1a2eed65950e321621) ([spring5-light-batch](https://github.com/jonathanlermitage/manon/tree/spring5-light-batch) branch)
  * use an **additional datasource to handle Spring Batch** tables (via Spring Boot 2.2 new `@BatchDataSource` annotation), and **initialize two datasources via Flyway**. See last commit of [spring5-light-extra-datasource-for-springbatch](https://github.com/jonathanlermitage/manon/tree/spring5-light-extra-datasource-for-springbatch) branch
* **Spring [Cache](https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache)** via Redis for application, and prefer an embedded cache provider during tests. See commits [a911f6a](https://github.com/jonathanlermitage/manon/commit/a911f6a08ce67b3b302f4ea3d17a73e8a0dcd6e6), [7e26822](https://github.com/jonathanlermitage/manon/commit/7e268222a745e5bbb88129d99b91379bafac7f58) and [ae6e0e6](https://github.com/jonathanlermitage/manon/commit/ae6e0e69ac37dbe44b51f449600943e09b9b149b) ([spring5-redis](https://github.com/jonathanlermitage/manon/tree/spring5-redis) branch)
* **Spring Retry**. See last commit of [spring5-light-retry](https://github.com/jonathanlermitage/manon/tree/spring5-light-retry) branch
* **[JWT (JSON Web Token) authentication](https://jwt.io/)** (migration from Auth Basic). See last commit of [spring5-light-jwt-auth](https://github.com/jonathanlermitage/manon/tree/spring5-light-jwt-auth) branch
  * manage JWT in database to invalidate living tokens when needed (e.g. a user can disconnect his multiple sessions). See last commit of [spring5-light-jwt-managed-in-db](https://github.com/jonathanlermitage/manon/tree/spring5-light-jwt-managed-in-db) branch
  * add some cache (Redis server or in-memory map). See last commit of [spring5-light-redis-cache-on-jwt-refs](https://github.com/jonathanlermitage/manon/tree/spring5-light-redis-cache-on-jwt-refs) branch
* **[Flyway](https://flywaydb.org/)** support (database migration). See last commit of [spring5-light-flyway](https://github.com/jonathanlermitage/manon/tree/spring5-light-flyway) branch
* **[GraphQL](https://www.graphql-java.com)** support (queries and mutations). See last commit of [spring5-light-graphql](https://github.com/jonathanlermitage/manon/tree/spring5-light-graphql) branch. Please note that you may see instrumentation warnings during JaCoCo test coverage, and some frameworks don't work: cant use Mockito's `SpyBean` ([issue](https://github.com/graphql-java-kickstart/graphql-java-servlet/issues/161)) on a Spring service used by GraphQL; can't load Flyway. That's why it's not merged into main branch yet
* send **emails** to a **[GreenMail](http://www.icegreen.com/greenmail/)** sand boxed email server and check retrieval. See last commit of [spring5-light-mailing](https://github.com/jonathanlermitage/manon/tree/spring5-light-mailing) branch
* **Asynchronous Methods** support. See last commit of [spring5-light-async](https://github.com/jonathanlermitage/manon/tree/spring5-light-async) branch
* **Integration Tests** and (some) **Unit Tests** via **[JUnit5](https://junit.org/junit5/)** and **[REST Assured](http://rest-assured.io)**. I prefer TestNG for its keywords, dataproviders and maturity, but JUnit is the most used testing framework. REST Assured helped me to test API without Spring's magic
  * **migration from [TestNG](https://testng.org) to [JUnit5](https://junit.org/junit5/)** (with Spring Boot, dataproviders, expected exceptions, beforeClass and afterClass annotations). See last commit of [spring5-light-testng-to-junit5](https://github.com/jonathanlermitage/manon/tree/spring5-light-testng-to-junit5) branch
  * some **test parallelization with JUnit5**. See last commit of [spring5-light-junit5-parallel-execution](https://github.com/jonathanlermitage/manon/tree/spring5-light-junit5-parallel-execution) branch
  * Java **architecture tests** via [**ArchUnit**](https://github.com/TNG/ArchUnit). See last commit of [spring5-light-archunit](https://github.com/jonathanlermitage/manon/tree/spring5-light-archunit) branch
  * **JSON tests** via [**JsonUnit**](https://github.com/lukas-krecan/JsonUnit#hamcrests-matchers) Hamcrests matchers. See last commit of [jsonunit](https://github.com/jonathanlermitage/manon/tree/jsonunit) branch
  * use dockerized **MariaDB** on **[Travis](https://travis-ci.org/jonathanlermitage/manon)** CI instead of embedded HSQLDB. See last commit of [spring5-light-travis-with-mariadb](https://github.com/jonathanlermitage/manon/tree/spring5-light-travis-with-mariadb) branch
  * use dockerized **PostgreSQL** on **[Travis](https://travis-ci.org/jonathanlermitage/manon)** CI. See last commit of [postgres](https://github.com/jonathanlermitage/manon/tree/postgres) branch
  * ~~**split Unit and Integration Tests**: run Unit Tests first and, if they don't fail, run Integration Tests. See last commit of [spring5-light-separate-integ-unit](https://github.com/jonathanlermitage/manon/tree/spring5-light-separate-integ-unit) branch. Due to JDK11+JUnit5+Failsafe issues ([that may be fixed in Failsafe 3.0.0](https://maven.apache.org/surefire/maven-failsafe-plugin/)), I run both Integration and Unit tests via Surefire Maven plugin~~ I now use Surefire + Failsafe, and it works well (see commit [fafac63](https://github.com/jonathanlermitage/manon/commit/fafac63c1a0adb8debeba6782840f0406b44c930))
  * use [**java-faker**](https://github.com/DiUS/java-faker) to generate random test data. See last commit of [javafaker](https://github.com/jonathanlermitage/manon/tree/javafaker) branch
  * log web requests (client info, headers, query string, payload) during tests. See last commit of [log-web-requests](https://github.com/jonathanlermitage/manon/tree/log-web-requests) branch
* **[Gatling](https://gatling.io) benchmark**. See last commit of [spring5-light-gatling](https://github.com/jonathanlermitage/manon/tree/spring5-light-gatling) branch and dedicated [README.md](docker/gatling/README.md) file
* **migration from MongoDB** (used to store regular and authentication data) to **MariaDB**. See last commit of [spring5-light-mongo-to-sql](https://github.com/jonathanlermitage/manon/tree/spring5-light-mongo-to-sql) branch
  * tests work with an **embedded MongoDB** instance (for data) and HSQLDB (for Spring Batch internals only), that means you don't have to install any database to test project
    * use **embedded MongoDB** during tests. See commits [37e1be5](https://github.com/jonathanlermitage/manon/commit/37e1be5f01c3ffa6ecf4d9c3e558b4ffb297227f) and [161d321](https://github.com/jonathanlermitage/manon/commit/161d3214ab72e76a2f041bbe8914077137513fb7) ([spring5-embedmongo](https://github.com/jonathanlermitage/manon/tree/spring5-embedmongo) branch)
    * make embedded MongoDB work with version from 3.6 to 4.0: see commit [a75a917](https://github.com/jonathanlermitage/manon/commit/a75a9178211233c24a6ac7001559fdfdf3413cd2) ([spring5-light-mongo4.0.x](https://github.com/jonathanlermitage/manon/tree/spring5-light-mongo4.0.x) branch)
* replace **HSQLDB** by **H2**. See commit [ae4701e](https://github.com/jonathanlermitage/manon/commit/ae4701e6b0ed490aed32c5b07c84c5b52711188b) ([spring5-light-hsqldb-to-h2](https://github.com/jonathanlermitage/manon/tree/spring5-light-hsqldb-to-h2) branch)
* migration to **Java 8 new [Date and Time API](https://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html) ([JSR-310](https://jcp.org/en/jsr/detail?id=310))**. See last commit of [spring5-light-java8-datetime-api](https://github.com/jonathanlermitage/manon/tree/spring5-light-java8-datetime-api) branch
* integration with some free (for open-source) services like **[AppVeyor](https://ci.appveyor.com/project/jonathanlermitage/manon)** (Windows CI), **[Travis](https://travis-ci.org/jonathanlermitage/manon)** (Linux and MacOS CI), **[Cirrus](https://cirrus-ci.com)** (CI), **[MS Azure DevOps Pipelines](https://dev.azure.com/jonathanlermitage/manon/_build?definitionId=1&view=buildsHistory)** (CI), **[CodeCov](https://codecov.io/gh/jonathanlermitage/manon)** (code coverage), **[SonarCloud](https://sonarcloud.io/dashboard?id=nanon:manon)** (code quality), **[LGTM](https://lgtm.com/)** (code quality), **[Better Code Hub](https://bettercodehub.com)** (code quality)  
* [Maven](https://github.com/takari/maven-wrapper) and Gradle wrappers, and a `do` Bash script that helps you to launch some usefull commands
* **code coverage** thanks to **JaCoCo** Maven and Gradle plugin
* some **AOP** to capture performance of API endpoints
* **Spring Actuator** web endpoints configured
  * show **Git information** (branch, commit...) into Actuator's `info` endpoint. See last commit of [actuator-git-info](https://github.com/jonathanlermitage/manon/tree/actuator-git-info) branch 
* **Swagger UI** to provide documentation about REST API endpoints
  * run application and check [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html). See commit [834852c](https://github.com/jonathanlermitage/manon/commit/834852cd5ce8bbb869a189aecdd90097c9168152) ([spring5-swagger](https://github.com/jonathanlermitage/manon/tree/spring5-swagger) branch)
* **Docker** builds without Docker daemon thanks to **[Jib](https://github.com/GoogleContainerTools/jib)**. **Docker Compose** support
  * See [DEPLOY.md](DEPLOY.md) to package and run application. See commit [de7335b](https://github.com/jonathanlermitage/manon/commit/de7335b2be850ca6a7b683bdbe2b86adc990b594) ([spring5-light-docker-jib](https://github.com/jonathanlermitage/manon/tree/spring5-light-docker-jib) branch)
* **ELK** stack (**ElasticSearch Logstash Kibana**) via Docker plus Docker Compose to parse application and Nginx logs, and Cerebro to monitor ElasticSearch node
  * see [DEPLOY.md](DEPLOY.md) to package and run application. Also, see commits [6b76e37](https://github.com/jonathanlermitage/manon/commit/6b76e376566fd34b4b3521dc6c60eaf7c30c1c22) and [966969f](https://github.com/jonathanlermitage/manon/commit/966969fc16277be3ec8605592f5ed7ae90ba7024) ([spring5-light-elk](https://github.com/jonathanlermitage/manon/tree/spring5-light-elk) branch)
* [**Prometheus**](https://prometheus.io) to monitor metrics. They are fetched from Spring Boot Actuator's Prometheus endpoint. See last commit of [actuator-metrics-to-prometheus](https://github.com/jonathanlermitage/manon/tree/actuator-metrics-to-prometheus) branch
  * [**Grafana**]() to fetch and display metrics from Prometheus. See last commit of [prometheus-to-grafana](https://github.com/jonathanlermitage/manon/tree/prometheus-to-grafana) branch
* integration with **[SpotBugs](https://github.com/find-sec-bugs/find-sec-bugs/wiki/Maven-configuration)** ([FindBugs](http://findbugs.sourceforge.net))
* integration with **[Maven enforcer plugin](https://maven.apache.org/enforcer/maven-enforcer-plugin/)**. See last commit of [spring5-light-maven-enforcer-plugin](https://github.com/jonathanlermitage/manon/tree/spring5-light-maven-enforcer-plugin) branch
* integration with **[OWASP dependency check plugin](https://jeremylong.github.io/DependencyCheck/index.html)**. See last commit of [spring5-light-owasp-dependency-check-plugin](https://github.com/jonathanlermitage/manon/tree/spring5-light-owasp-dependency-check-plugin) branch
* Spring Boot dependency management without Spring Boot parent POM. See last commit of [spring5-light-spring-boot-dependencies](https://github.com/jonathanlermitage/manon/tree/spring5-light-spring-boot-dependencies) branch
* use [**Glowroot**](https://glowroot.org/) monitoring tool (via regular Docker build, not Jib). Run `./do docker up` then check [`http://localhost:4000`](http://localhost:4000). See last commit of [spring5-light-glowroot-docker](https://github.com/jonathanlermitage/manon/tree/spring5-light-glowroot-docker) branch

For fun and to show some skills :cat:

## Author

Jonathan Lermitage (<jonathan.lermitage@gmail.com>)  
Linkedin profile: [jonathan-lermitage-092711142](https://www.linkedin.com/in/jonathan-lermitage-092711142/)

## Compilation and test

First, install JDK8+ and Maven3+.
  
You can now use the `./do` Linux Bash script:  
```
do help         show this help message
do fixgit       set executable flag on git index for required files
do normgit      call git add --update --renormalize
do conv         generate a Dependency Convergence report in target/site/dependency-convergence.html
do oga          check for deprecated groupId and artifactId couples
do owasp        generate a OWASP dependencies vulnerabilities report in target/dependency-check-report.html
do t            test using embedded HSQLDB
do td           test using dockerized MariaDB and Redis (container is started and stopped by script)
do td-postgres  test using dockerized PostgreSQL and Redis (container is started and stopped by script)
do ut           run unit tests only, no integration tests
do tc           run unit + integration tests and generate coverage data
do itc          run integration tests only and generate coverage data
do gatling      benchmark application via a Gatling container (run './do up' first to start application)
do sc           compute and upload Sonar analysis to SonarCloud
do tsc          similar to "do tc" then "do sc"
do sb           scan with SpotBugs then show GUI
do b            build without testing
do c            clean
do p            package application to manon.jar
do rd           package and run application with dev-mariadb profile 
do w 3.5.2      set or upgrade Maven wrapper to 3.5.2
do cv           check plugins and dependencies versions
do uv           update plugins and dependencies versions
do dt           show dependencies tree
do rmi          stop Docker application, then remove its containers and images
do cdi          clean up dangling Docker images
do dockerreset  stop and remove all containers, remove all images and prune volumes
do docker       build Docker image with Dockerfile to a Docker daemon
do dockerpull   pull 3rd party Docker containers
do jib          build Docker image with Jib to a Docker daemon
do jibtar       build and save Docker image with Jib to a tarball
do up           create and start containers via docker-compose
do stop         stop containers via docker-compose
do upelk        create and start ELK containers via docker-compose
do stopelk      stop ELK containers via docker-compose
do upcerebro    create and start Cerebro container via docker-compose
do stopcerebro  stop Cerebro container via docker-compose
do maria        connect to dockerized MariaDB business database by calling MySQL Client provided by container
do maria-batch  connect to dockerized MariaDB Spring Batch database by calling MySQL Client provided by container
do mariah       connect to dockerized MariaDB business database by calling host MySQL Client (mysql-client package must be installed)
do mariah-batch connect to dockerized MariaDB Spring Batch database by calling host MySQL Client (mysql-client package must be installed)
```

Nota: the Linux Bash script can chain parameters, e.g.: `./do cdi rmi w 3.6.0 c tc docker up`.  
Nota: a Windows `do.cmd` script exists, but it's limited to some basic features. Run `do.cmd help` for details.  

## Tips

### Proxy

You may experience **connection issues** with many `do` commands that use **Maven Wrapper** if you are behind a **proxy**. Maven Wrapper doesn't pick proxy configuration from Maven's settings, so you should give some proxy parameters to JVM. See this excellent [Stack Overflow answer](https://stackoverflow.com/questions/41187743/jhipster-configure-maven-wrapper-proxy/44500269#44500269).

### Git

Some usefull git alias to put into your `.gitconfig` file:

```bash
[alias]

# Show a pretty commit log
ls = log --pretty=format:"%C(green)%h\\ %C(yellow)[%ad]%Cred%d\\ %Creset%s%C(cyan)\\ [%cn]" --decorate --date=relative

# Checkout a merge request, example: git mr upstream 6
mr = !sh -c 'git fetch $1 merge-requests/$2/head:mr-$1-$2 && git checkout mr-$1-$2' -

# Credit an author on the latest commit
credit = "!f() { git commit --amend --author \"$1 <$2>\" -C HEAD; }; f"

# Interactive rebase with the given number of latest commits
reb = "!r() { git rebase -i HEAD~$1; }; r"

# Show the diff between the latest commit and the current state
d = !"git diff-index --quiet HEAD -- || clear; git diff --patch-with-stat"
```

## License

MIT License. In other words, you can do what you want: this project is entirely OpenSource, Free and Gratis.
