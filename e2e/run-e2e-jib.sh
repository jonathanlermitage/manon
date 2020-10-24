#!/bin/bash

echo "-------------------------------------"
echo "  Run e2e tests with Docker build"
echo "-------------------------------------"

echo "Buil project and application image with Jib"
docker rm $(docker ps -a | grep "lermitage-manon" | awk '{print $1}')
docker rmi $(docker images | grep -E "^lermitage-manon|<none>" | awk '{print $3}')
sh ./mvnw compile jib:dockerBuild -DskipUT=true -DskipIT=true -U -P jib

./e2e/_e2e-executor.sh

exit $?
