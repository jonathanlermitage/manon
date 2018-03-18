[![AppVeyor Build status](https://ci.appveyor.com/api/projects/status/3tfcq04yte3ff1iq?svg=true)](https://ci.appveyor.com/project/jonathanlermitage/manon) [![Travis Build status](https://travis-ci.org/jonathanlermitage/manon.svg?branch=spring5)](https://travis-ci.org/jonathanlermitage/manon) 
[![codecov](https://codecov.io/gh/jonathanlermitage/manon/branch/spring5/graph/badge.svg)](https://codecov.io/gh/jonathanlermitage/manon) [![license](https://img.shields.io/github/license/jonathanlermitage/manon.svg)](https://github.com/jonathanlermitage/manon/blob/master/LICENSE.txt)

1. [Project](https://github.com/jonathanlermitage/manon#project)  
2. [Author](https://github.com/jonathanlermitage/manon#author)
3. [Branches](https://github.com/jonathanlermitage/manon#branches)
4. [Compilation and test](https://github.com/jonathanlermitage/manon#compilation-and-test)
5. [License](https://github.com/jonathanlermitage/manon#license)

## Project

Some experimentation with Spring Boot 2, JDK9, NoSQL, etc. It demonstrates usage of:

* **Maven** or **Gradle** build tools (still not chosen my favorite, that's why you'll see two active branches)
* **Spring Boot 2** + Spring Framework 5 and migration from Spring Boot 1 + Spring Framework 4
* **Java 9** and migration from Java 8
* Spring Web, **REST** API
* Spring **Security**, to authenticate users via auth_basic, and fetch authentication data from MongoDB instead of default SQL database 
* Spring Data to serve data from a **MongoDB** database
* Spring **Batch** to schedule and manage some tasks
* Spring **Cache** via Redis
* **integration tests** and (some) unit-test via **TestNG**, because TestNG is so much superior to JUnit4/5... (better keywords, dataproviders, old/mature and very stable API, easily configurable, test suites)
* tests work with an **embedded MongoDB** instance (for data) and HSQLDB (for Spring Batch internals only), that means you don't have to install any database to test project, simply run `mvnw test` 
* integration with some free (for open-source) third-party **CI** like **AppVeyor** (Windows) and **Travis** (Linux)
* Maven and Gradle wrappers, and a `do.cmd` script that helps you to start some usefull commands
* code coverage thanks to **JaCoCo** Maven and Gradle plugin and integration with **CodeCov**
* some **AOP** to capture performance of API endpoints
* Spring **Actuator** web endpoints configured
* **Swagger UI** to provide documentation about REST API endpoints

For fun and to show some skills :cat:

## Author

Jonathan Lermitage (<jonathan.lermitage@gmail.com>)  
**Your're Canadian or American and wanna hire an experienced French devops? Contact me! I'm okay to move.**  
Linkedin profile: [jonathan-lermitage-092711142](https://www.linkedin.com/in/jonathan-lermitage-092711142/)

## Branches

* active
  * [spring5](https://github.com/jonathanlermitage/manon/tree/spring5): main branch, based on Spring Framework 5, **Spring Boot 2** and **JDK9**
  * [spring5-mvn-to-gradle](https://github.com/jonathanlermitage/manon/tree/spring5-mvn-to-gradle): migration from **Maven to Gradle**. Stable and functional, but still needs some improvements
* merged into [spring5](https://github.com/jonathanlermitage/manon/tree/spring5)
  * [spring5-embedmongo](https://github.com/jonathanlermitage/manon/tree/spring5-embedmongo): use **embedded MongoDB** during tests. See commits [37e1be5](https://github.com/jonathanlermitage/manon/commit/37e1be5f01c3ffa6ecf4d9c3e558b4ffb297227f) and [161d321](https://github.com/jonathanlermitage/manon/commit/161d3214ab72e76a2f041bbe8914077137513fb7)
  * [spring5-swagger](https://github.com/jonathanlermitage/manon/tree/spring5-swagger): enable **Swagger UI**. Run application and check `http://localhost:8080/swagger-ui.html`, authenticate with `ROOT` / `woot`. See commit [429ae53](https://github.com/jonathanlermitage/manon/commit/429ae53bc5211d8d97e8ccca20a4b183f207c6ee)
* archived
  * [spring4](https://github.com/jonathanlermitage/manon/tree/spring4): previous active branch, based on Spring Framework 4, Spring Boot and JDK8
  * [spring4-redis-cache](https://github.com/jonathanlermitage/manon/tree/spring4-redis-cache): enable caching based on **Redis** server. See commit [0f1eff7](https://github.com/jonathanlermitage/manon/commit/0f1eff768e73a69e07016e153b825a131146a63a)
* other branches may appear and disappear quickly, depending on experimentation results and motivation ;-)

## Compilation and test

To build and test, simply run `mvnw test`. To enable JaCoCo code coverage, activate the `coverage` profile: `mvnw test -Pcoverage`. Tested on Travis (Linux) and AppVeyor (Windows) CI.
  
On Windows, you can use the `do.cmd` script:  
```
do help      show this help message
do t         test without code coverage
do tc        test with code coverage
do b         build without testing
do c         clean
do p         package application to manon.jar
do w 3.5.2   set or upgrade Maven wrapper to 3.5.2
do cv        check plugins and dependencies versions
do uv        update plugins and dependencies versions
do dt        show dependencies tree
```

## License

MIT License. In other words, you can do what you want: this project is entirely OpenSource, Free and Gratis.
