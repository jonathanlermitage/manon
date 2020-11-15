#!/bin/bash

echo "-------------------------------------"
echo "  Run e2e tests with Docker build"
echo "-------------------------------------"

echo "Build project"
sh ./mvnw package -DskipUT=true -DskipIT=true -U

echo "Buil application image from Dockerfile"
docker rm $(docker ps -a | grep "lermitage-manon" | awk '{print $1}')
docker rmi $(docker images | grep -E "^lermitage-manon|<none>" | awk '{print $3}')
docker build -f ./docker/Dockerfile -t lermitage-manon:1.0.0-SNAPSHOT .

./e2e/_e2e-executor.sh

exit $?
