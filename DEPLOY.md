## How do deploy and run application

This document will help you to run application manually or via Docker Compose.  
First, go to project's root and make the `./do` utility script executable if needed: `chmod +x ./do`.

### Manually

* Install recent **JDK11**.
* Install **MariaDB** (any reasonably recent version should work) then create `manon_dev` and `manon_dev_batch` schemas.
* Package and run application via `./do rd`. Application will start on port 8080 with `dev` Spring profile.
  * To run with another Spring profile (e.g. `prod`), package application via `./do p`, go to `target/` directory and run `java -jar -Xms128m -Xmx512m -Dspring.profiles.active=prod manon.jar`.

### Docker Compose 

* application + Nginx + MariaDB + Redis + Greenmail + Prometheus
* log analysis via ELK + Cerebro

Application dockerized with [Jib](https://github.com/GoogleContainerTools/jib) and OpenJDK11, two [MariaDB](https://downloads.mariadb.org) databases (one for business tables, an other for Spring Batch tables), [Nginx](http://nginx.org/en/download.html) as HTTP proxy, [Redis](https://redis.io) cache, [Greenmail](https://github.com/greenmail-mail-test/greenmail) server to fake email messaging, an ELK stack to parse logs, and [Prometheus](https://prometheus.io) to monitor metrics provided by Spring Boot Actuator. To proceed, follow these steps:

#### Preparation: create directories and install software

* Elasticsearch may need `sudo sysctl -w vm.max_map_count=262144`.
* Install **Docker**:
  ```bash
  # install Docker Community Edition, tested on Lubuntu 18.04 LTS
  sudo apt-get remove docker docker-engine docker.io
  sudo apt-get install apt-transport-https ca-certificates curl software-properties-common
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
  sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
  sudo apt-get update
  sudo apt-get install docker-ce
  sudo groupadd docker 
  sudo usermod -aG docker $(whoami)
  sudo systemctl enable docker
  ```
* Install **Docker Compose**:
  ```bash
  sudo rm /usr/local/bin/docker-compose
  sudo curl -L "https://github.com/docker/compose/releases/download/1.23.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
  ```
 
#### Build and deploy application
  
* Build and install application image:
  * via Jib: `./do jib`.
  * or via traditional `Dockerfile`: `./do docker`.
* Edit `docker-compose.yml` if needed (e.g. to customize ports).
* Then run application image and dependencies via Docker Compose: `./do up` (it actually does: `docker-compose -f ./docker/docker-compose.yml up -d`).
* Application listens on port 8081 and its logs are stored in `~/manon-app-logs/`.
* Check application healthcheck: wait 40s, then run `docker inspect --format "{{json .State.Health }}" manon | jq`. 
  * Please note that healthcheck won't work if you built image with Jib: `openjdk` image doesn't contain `curl` software, I installed it in `Dockerfile` only.
* Check application connectivity by visiting `http://localhost:8081/actuator/health`.
* Replace `8081` by `8000` to access application via Nginx proxy.
* Check Nginx error and access logs in `~/manon-nginx-logs`.
* Launch a batch (e.g. `userSnapshotJob`) `curl -X POST http://localhost:8000/api/v1/sys/batch/start/userSnapshotJob --header "Authorization: Bearer REPLACE_BY_ADMIN_TOKEN"` then check the `user_stats` and `user_snapshot` MariaDB tables.
* Connect to MariaDB business database: `./do maria` (it connects to database via container's MySQL Client). You can now query tables.
* Connect to MariaDB Spring Batch database: `./do maria-batch` (it connects to database via container's MySQL Client). You can now query tables.
* Play with Prometheus by visiting `http://localhost:9090` (go to Alerts or Graph, then use auto-completion to fetch some data, e.g. "system_cpu_usage"). Check `http://localhost:9090/targets` to ensure both `prometheus` and `spring-actuator` endpoints are UP.
* Play with Grafana by visiting `http://localhost:3000`
  * log in with `admin`/`admin`
  * go to `Configuration`, `Data Sources`, `Add data source`, choose `Prometheus`, name: `Prometheus` (case is important), url: `http://prometheus:9090`, `Save & Test`
  * go to `Dashboards`, `Manage`, `Import`, past content of [manon-dashboard.json](docker/grafana/manon-dashboard.json) file, `Load`, `Import`
  * go to `Dashboards`, `Home`, open the `manon` dahsboard: you should see JDBC and CPU metrics

#### Deploy ELK stack and Cerebro

* Run ELK stack images via Docker Compose: `./do upelk`.
* Visit `http://localhost:5601` and go to `Dev Tools`. You can now send queries to Elasticsearch to find some logs:
  * Get application logs via: `GET /manon-app-*/_search`.
  * Get Nginx access logs via: `GET /manon-nginx-access-*/_search`.
  * You can delete these logs via: `DELETE /manon*`. Play with application and show logs again.
* Optional: run Cerebro via Docker Compose: `./do upcerebro`.
  * Visit `http://localhost:9000` and select `Main Cluster` (it's an alias for `http://elasticsearch:9200`, see `docker/cerebro/cerebro.conf` file for details).

You can now stop images via `./do stopcerebro` (Cerebro), `./do stopelk` (ELK stack), `./do stop` (application and dependencies).
