## Gatling benchmark

Files used for application benchmark via [Gatling](https://gatling.io/) 3.2.1.

* `conf/`: Gatling 3.2.1 default configuration files
* `user-files/simulations/`: benchmark scenario. Written in Scala

Benchmark is run via a Docker Gatling image against a living application which is also running via Docker. See [docker-compose-gatling.yml](../docker-compose-gatling.yml) and [docker-compose.yml](../docker-compose.yml) for details.

Tip: to work with Scala scenario file in IntelliJ (Community and Ultimate) and get code assistance, you can follow these steps:
1. install the **Scala** plugin (by JetBrains)
2. download and extract Gatling 3.2.1 ZIP file, then configure IntelliJ to add Gatling `lib/` folder to globals libraries (see *Project Structure* > *Platform Settings* > *Globals Libraries* > add a *Standard library*)

Similar instructions should work with other IDEs.
