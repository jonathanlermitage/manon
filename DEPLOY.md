## How do deploy and run application

This document will help you to run application.

### Manual run

* Install **JDK8** and **MongoDB 3.4.x**. MongoDB should listen on port 27017, accept username `root` and password `woot` on the `manondev` database (authentication and data). See `src/main/resources/application-dev.yml` for details.  
* Package and run application via `do rd`. Application will start on port 8080 with `dev` Spring profile.
  * To run with another Spring profile (e.g. `prod`), package application via `do p`, go to `target/` directory and run `java -jar -Xms128m -Xmx512m -Dspring.profiles.active=prod,metrics -Dfile.encoding=UTF-8 -Djava.awt.headless=true manon.jar`.

### Docker

* Install **Docker** Community Edition:
  ```bash
  # tested on Lubuntu 18.04 LTS
  sudo apt-get remove docker docker-engine docker.io
  sudo apt-get install apt-transport-https ca-certificates curl software-properties-common
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
  sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
  sudo apt-get update && sudo apt-get install docker-ce
  sudo groupadd docker && sudo usermod -aG docker jon  # replace 'jon' by your user name (see whoami)
  sudo systemctl enable docker
  ```
* Run a **MongoDB 3.4.x** docker image:
  ```bash
  # MongoDB 3.4.17
  # find tags at https://github.com/docker-library/docs/blob/master/mongo/README.md
  mkdir ~/data
  docker run -d --net=host -p 27017:27017 -v ~/data:/data/mongodb mongo:3.4.17-jessie
  # optional: install MongoDB command-line client and check connectivity
  sudo apt-get install mongodb-clients
  mongo localhost/manon
  ```
* Build and install application image via `do jib`.  
* Then run **application image**: 
  ```bash
  mkdir ~/logs
  docker run -d --name lermitage-manon --net=host -p 8080:8080 -v ~/logs:/logs lermitage-manon:1.0.0-SNAPSHOT
  ```
  * To change Spring profiles, e.g. with `dev` profile, add `-e "SPRING_PROFILES_ACTIVE=dev"` to the `docker run` command.
* check connectivity by visiting `http://localhost:8080/actuator/health` (default login/password is `ROOT/woot`).
* MongoDB data is persisted in `~/data` and applications logs are in `~/logs`.
